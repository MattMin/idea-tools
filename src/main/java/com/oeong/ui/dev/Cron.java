package com.oeong.ui.dev;

import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import com.intellij.openapi.util.text.StringUtil;
import com.oeong.notice.Notifier;
import lombok.Getter;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.cronutils.model.CronType.*;

public class Cron {
    private JComboBox<String> type;
    private JTextField expression;
    private JTextArea resultArea;
    @Getter
    private JPanel container;
    private JButton testBt;

    {
        testBt.addActionListener(this::getCron);
        definitionMap.keySet().forEach(type::addItem);
    }
    public void getCron(ActionEvent e){
        String text = expression.getText();
        if(StringUtil.isEmpty(text)){
            return;
        }
        String typeItem = (String) type.getSelectedItem();
        String expStr = expression.getText();
        String ret = exp(typeItem, expStr);
        resultArea.setText(ret);
    }

    static DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    static Map<String,CronDefinition> definitionMap = new HashMap<>();
    static {
        CronDefinition unix = CronDefinitionBuilder.instanceDefinitionFor(UNIX);
        definitionMap.put("UNIX",unix);
        CronDefinition spring = CronDefinitionBuilder.instanceDefinitionFor(SPRING);
        definitionMap.put("Java",spring);
        CronDefinition spring53 = CronDefinitionBuilder.instanceDefinitionFor(SPRING53);
        definitionMap.put("Spring",spring53);
        CronDefinition quartz = CronDefinitionBuilder.instanceDefinitionFor(QUARTZ);
        definitionMap.put("Quartz",quartz);
    }
    String exp(String type,String cron){
        try{
            CronDefinition definition = definitionMap.get(type);
            CronParser parser = new CronParser(definition);
            com.cronutils.model.Cron c = parser.parse(cron);
            ExecutionTime executionTime = ExecutionTime.forCron(c);
            StringBuilder sb = new StringBuilder();
            ZonedDateTime now = ZonedDateTime.now();
            Optional<ZonedDateTime> zonedDateTimeOptional = executionTime.nextExecution(now);
            if(zonedDateTimeOptional.isPresent()){
                ZonedDateTime zonedDateTime = zonedDateTimeOptional.get();
                sb.append(format.format(zonedDateTime)).append("\r\n");
                for(int i=0;i<4;i++){
                    zonedDateTimeOptional = executionTime.nextExecution(zonedDateTime);
                    if(zonedDateTimeOptional.isPresent()){
                        ZonedDateTime time = zonedDateTimeOptional.get();
                        sb.append(format.format(time)).append("\r\n");
                        zonedDateTime = time;
                    }
                }
            }
            return sb.toString();
        }catch (Exception e){
            Notifier.notifyError(e.getMessage());
        }
        return "";
    }

}
