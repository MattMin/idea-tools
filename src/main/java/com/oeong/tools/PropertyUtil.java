package com.oeong.tools;

import cn.hutool.json.JSONUtil;
import com.google.common.collect.Lists;
import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.credentialStore.CredentialAttributesKt;
import com.intellij.credentialStore.Credentials;
import com.intellij.ide.passwordSafe.PasswordSafe;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.project.Project;
import com.oeong.service.ApiInfosService;
import com.oeong.vo.ApiInfo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * @author mzyupc@163.com
 */
public class PropertyUtil {
    /**
     * connection id集合的key
     */
    private static final String CONNECTION_ID_LIST_KEY = "connectionIds";

    private static final String RELOAD_AFTER_ADDING_THE_KEY = "reloadAfterAddingTheKey";

    private PropertiesComponent properties;


    private ApiInfosService apiInfosService;

    private PropertyUtil(Project project) {
        properties = PropertiesComponent.getInstance(project);
        apiInfosService = ApiInfosService.getInstance(project);
    }

    public static PropertyUtil getInstance(Project project) {
        PropertyUtil propertyUtil = new PropertyUtil(project);

        // 迁移之前的配置
        // 迁移RELOAD_AFTER_ADDING_THE_KEY
        if (propertyUtil.properties.isValueSet(RELOAD_AFTER_ADDING_THE_KEY)) {
            propertyUtil.properties.unsetValue(RELOAD_AFTER_ADDING_THE_KEY);
        }

        // 迁移CONNECTION_ID_LIST_KEY
        final List<ApiInfo> connections = propertyUtil.getConnectionsOld();
        if (!connections.isEmpty()) {
            final List<ApiInfo> newConnections = propertyUtil.apiInfosService.getApiInfos();
            for (ApiInfo connection : connections) {
                connection.setGlobal(false);
                propertyUtil.removeConnectionOld(connection.getId());
                newConnections.add(connection);
            }
            propertyUtil.properties.unsetValue(CONNECTION_ID_LIST_KEY);
        }
        return propertyUtil;
    }

    /**
     * 返回所有已配置的连接id
     *
     * @return 连接列表元素
     */
    public List<ApiInfo> getConnectionsOld() {
        List<String> ids = properties.getList(CONNECTION_ID_LIST_KEY);
        if (CollectionUtils.isEmpty(ids)) {
            return Lists.newArrayList();
        }

        List<ApiInfo> result = new ArrayList<>();
        for (String id : ids) {
            String connection = properties.getValue(id);
            if (StringUtils.isEmpty(connection)) {
                removeConnectionOld(id);
                continue;
            }
            result.add(JSONUtil.toBean(connection, ApiInfo.class));
        }
        return result;
    }

    public List<ApiInfo> getConnections() {
        final List<ApiInfo> connections = apiInfosService.getApiInfos();
        if (connections.isEmpty()) {
            return Lists.newArrayList();
        }

        for (ApiInfo connection : connections) {
            // connectionInfo 如果有 password 则将 connection 中存储的 password 删除, 使用 PasswordSafe 存储 password
            if (StringUtils.isEmpty(connection.getApiSecret())) {
                String password = retrievePassword(connection.getId());
                connection.setApiSecret(password);
            }
        }

        return connections;
    }

    public ApiInfo getGlobalConnection() {
        final List<ApiInfo> connections = apiInfosService.getApiInfos();
        if (connections.isEmpty()) {
            return null;
        }

        ApiInfo apiInfo = null;
        for (ApiInfo connection : connections) {
            if (connection.getGlobal()) {
                apiInfo = connection;
            }
        }

        // connectionInfo 如果有 password 则将 connection 中存储的 password 删除, 使用 PasswordSafe 存储 password
        if (apiInfo != null && StringUtils.isEmpty(apiInfo.getApiSecret())) {
            String password = retrievePassword(apiInfo.getId());
            apiInfo.setApiSecret(password);
        }

        return apiInfo;
    }

    /**
     * 保存连接信息
     *
     * @param apiInfo 连接信息
     * @return 连接ID
     */
    public String saveConnection(ApiInfo apiInfo) {
        if (apiInfo == null) {
            return null;
        }

        String connectionInfoId = apiInfo.getId();
        if (StringUtils.isEmpty(connectionInfoId)) {
            connectionInfoId = UUID.randomUUID().toString();
            apiInfo.setId(connectionInfoId);
        }

        // Brainless deletion
        apiInfosService.getApiInfos().remove(apiInfo);

        // 保存密码
        savePassword(connectionInfoId, apiInfo.getApiSecret());

        // 保存 connection
        apiInfosService.getApiInfos().add(apiInfo);
        return connectionInfoId;
    }

    /**
     * 删除连接
     *
     * @param id 连接ID
     */
    public void removeConnectionOld(String id) {
        List<String> ids = properties.getList(CONNECTION_ID_LIST_KEY);
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }

        if (!ids.contains(id)) {
            return;
        }

        ids.remove(id);
        properties.setList(CONNECTION_ID_LIST_KEY, ids);
        properties.unsetValue(id);
    }

    public void removeConnection(ApiInfo apiInfo) {
        apiInfosService.getApiInfos().remove(apiInfo);
        savePassword(apiInfo.getId(), null);
    }

    /**
     * 查询连接
     *
     * @param id 连接ID
     * @return 连接信息
     */
    public ApiInfo getConnection(String id) {
        if (StringUtils.isEmpty(id)) {
            return null;
        }

        final Map<String, ApiInfo> collect = getConnections().stream()
                .collect(Collectors.toMap(ApiInfo::getId, Function.identity()));

        ApiInfo apiInfo = collect.get(id);
        return apiInfo;
    }


    private CredentialAttributes createCredentialAttributes(String connectionId) {
        return new CredentialAttributes(
                CredentialAttributesKt.generateServiceName("Mulan", connectionId)
        );
    }

    /**
     * 获取密码
     *
     * @return 密码, 如果是 "" 则返回 null
     */
    private String retrievePassword(String connectionId) {
        CredentialAttributes credentialAttributes = createCredentialAttributes(connectionId);
        String password = PasswordSafe.getInstance().getPassword(credentialAttributes);
        return StringUtils.isEmpty(password) ? null : password;
    }

    /**
     * 保存密码, 如果 password 是 null 或者 "", 则表示移除 password
     */
    private void savePassword(String connectionId, String password) {
        CredentialAttributes credentialAttributes = createCredentialAttributes(connectionId);
        Credentials credentials = null;
        if (StringUtils.isNotEmpty(password)) {
            credentials = new Credentials(connectionId, password);
        }
        PasswordSafe.getInstance().set(credentialAttributes, credentials);
    }

}
