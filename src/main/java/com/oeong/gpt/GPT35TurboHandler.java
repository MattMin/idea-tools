package com.oeong.gpt;

import com.intellij.openapi.util.text.StringUtil;
import com.oeong.gpt.ui.MainPanel;
import com.oeong.gpt.ui.MessageComponent;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public class GPT35TurboHandler extends AbstractHandler {
    private static final Logger LOG = LoggerFactory.getLogger(GPT35TurboHandler.class);

    public Call handle(MainPanel mainPanel, MessageComponent component, String question) {

        Call call = null;
        RequestProvider provider = new RequestProvider().create(mainPanel, question);
        try {
            LOG.info("GPT 3.5 Turbo Request: question={}",question);
            Request request = new Request.Builder()
                    .url(provider.getUrl())
                    .headers(Headers.of(provider.getHeader()))
                    .post(RequestBody.create(provider.getData().getBytes(StandardCharsets.UTF_8),
                                    MediaType.parse("application/json")))
                    .build();
            OpenAISettingsState instance = OpenAISettingsState.getInstance();
            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .connectTimeout(Integer.parseInt(instance.connectionTimeout), TimeUnit.MILLISECONDS)
                    .readTimeout(Integer.parseInt(instance.readTimeout), TimeUnit.MILLISECONDS);
//            builder.hostnameVerifier(getHostNameVerifier());
            builder.sslSocketFactory(getSslContext().getSocketFactory(), (X509TrustManager) getTrustAllManager());
//            if (instance.enableProxy) {
//                Proxy proxy = getProxy();
//                builder.proxy(proxy);
//            }

            OkHttpClient httpClient = builder.build();
            call = httpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    String errorMessage = StringUtil.isEmpty(e.getMessage())? "None" : e.getMessage();
                    if (e instanceof SocketException) {
                        LOG.info("GPT 3.5 Turbo: Stop generating");
                        component.setContent("Stop generating");
                        e.printStackTrace();
                        return;
                    }
                    LOG.error("GPT 3.5 Turbo Request failure. Url={}, error={}",
                            call.request().url(),
                            errorMessage);
                    errorMessage = "GPT 3.5 Turbo Request failure, cause: " + errorMessage;
                    component.setSourceContent(errorMessage);
                    component.setContent(errorMessage);
                    mainPanel.aroundRequest(false);
                    component.scrollToBottom();
                    mainPanel.getExecutorService().shutdown();
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String responseMessage = response.body().string();
                    LOG.info("GPT 3.5 Turbo Response: answer={}",responseMessage);
                    if (response.code() != 200) {
                        LOG.info("GPT 3.5 Turbo: Request failure. Url={}, response={}",provider.getUrl(), responseMessage);
                        component.setContent("Response failure, please try again. Error message: " + responseMessage);
                        mainPanel.aroundRequest(false);
                        return;
                    }
                    OfficialParser.ParseResult parseResult = OfficialParser.
                            parseGPT35Turbo(responseMessage);

                    mainPanel.getContentPanel().getMessages().add(OfficialBuilder.assistantMessage(parseResult.getSource()));
                    component.setSourceContent(parseResult.getSource());
                    component.setContent(parseResult.getHtml());
                    mainPanel.aroundRequest(false);
                    component.scrollToBottom();
                }
            });
        } catch (Exception e) {
            component.setSourceContent(e.getMessage());
            component.setContent(e.getMessage());
            mainPanel.aroundRequest(false);
        } finally {
            mainPanel.getExecutorService().shutdown();
        }
        return call;
    }
}
