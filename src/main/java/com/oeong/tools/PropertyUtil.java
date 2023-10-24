package com.oeong.tools;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.credentialStore.CredentialAttributesKt;
import com.intellij.credentialStore.Credentials;
import com.intellij.ide.passwordSafe.PasswordSafe;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.project.Project;
import com.oeong.service.ConnectionsService;
import com.oeong.vo.ConnectionInfo;
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


    private ConnectionsService connectionsService;

    private PropertyUtil(Project project) {
        properties = PropertiesComponent.getInstance(project);
        connectionsService = ConnectionsService.getInstance(project);
    }

    public static PropertyUtil getInstance(Project project) {
        PropertyUtil propertyUtil = new PropertyUtil(project);

        // 迁移之前的配置
        // 迁移RELOAD_AFTER_ADDING_THE_KEY
        if (propertyUtil.properties.isValueSet(RELOAD_AFTER_ADDING_THE_KEY)) {
            propertyUtil.properties.unsetValue(RELOAD_AFTER_ADDING_THE_KEY);
        }

        // 迁移CONNECTION_ID_LIST_KEY
        final List<ConnectionInfo> connections = propertyUtil.getConnectionsOld();
        if (!connections.isEmpty()) {
            final List<ConnectionInfo> newConnections = propertyUtil.connectionsService.getConnections();
            for (ConnectionInfo connection : connections) {
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
    public List<ConnectionInfo> getConnectionsOld() {
        List<String> ids = properties.getList(CONNECTION_ID_LIST_KEY);
        if (CollectionUtils.isEmpty(ids)) {
            return Lists.newArrayList();
        }

        List<ConnectionInfo> result = new ArrayList<>();
        for (String id : ids) {
            String connection = properties.getValue(id);
            if (StringUtils.isEmpty(connection)) {
                removeConnectionOld(id);
                continue;
            }
            result.add(JSON.parseObject(connection, ConnectionInfo.class));
        }
        return result;
    }

    public List<ConnectionInfo> getConnections() {
        final List<ConnectionInfo> connections = connectionsService.getConnections();
        if (connections.isEmpty()) {
            return Lists.newArrayList();
        }

        for (ConnectionInfo connection : connections) {
            // connectionInfo 如果有 password 则将 connection 中存储的 password 删除, 使用 PasswordSafe 存储 password
            if (StringUtils.isEmpty(connection.getApiSecret())) {
                String password = retrievePassword(connection.getId());
                connection.setApiSecret(password);
            }
        }

        return connections;
    }

    public ConnectionInfo getGlobalConnections() {
        final List<ConnectionInfo> connections = connectionsService.getConnections();
        if (connections.isEmpty()) {
            return null;
        }

        ConnectionInfo connectionInfo = null;
        for (ConnectionInfo connection : connections) {
            if (connection.getGlobal()) {
                connectionInfo = connection;
            }
        }

        // connectionInfo 如果有 password 则将 connection 中存储的 password 删除, 使用 PasswordSafe 存储 password
        if (connectionInfo != null && StringUtils.isEmpty(connectionInfo.getApiSecret())) {
            String password = retrievePassword(connectionInfo.getId());
            connectionInfo.setApiSecret(password);
        }

        return connectionInfo;
    }

    /**
     * 保存连接信息
     *
     * @param connectionInfo 连接信息
     * @return 连接ID
     */
    public String saveConnection(ConnectionInfo connectionInfo) {
        if (connectionInfo == null) {
            return null;
        }

        String connectionInfoId = connectionInfo.getId();
        if (StringUtils.isEmpty(connectionInfoId)) {
            connectionInfoId = UUID.randomUUID().toString();
            connectionInfo.setId(connectionInfoId);
        }

        // Brainless deletion
        connectionsService.getConnections().remove(connectionInfo);

        // 保存密码
        savePassword(connectionInfoId, connectionInfo.getApiSecret());

        // 保存 connection
        connectionsService.getConnections().add(connectionInfo);
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

    public void removeConnection(ConnectionInfo connectionInfo) {
        connectionsService.getConnections().remove(connectionInfo);
        savePassword(connectionInfo.getId(), null);
    }

    /**
     * 查询连接
     *
     * @param id 连接ID
     * @return 连接信息
     */
    public ConnectionInfo getConnection(String id) {
        if (StringUtils.isEmpty(id)) {
            return null;
        }

        final Map<String, ConnectionInfo> collect = getConnections().stream()
                .collect(Collectors.toMap(ConnectionInfo::getId, Function.identity()));

        ConnectionInfo connectionInfo = collect.get(id);
        return connectionInfo;
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
