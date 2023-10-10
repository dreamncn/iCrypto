/*
 * Created by JFormDesigner on Mon Sep 19 12:24:48 CST 2022
 */

package net.ankio.icrypto.ui;



import jdk.nashorn.internal.runtime.regexp.joni.Config;
import net.ankio.icrypto.BurpExtender;
import net.ankio.icrypto.rule.Rule;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;


/**
 * 插件的GUI实现
 * @author ankio
 */
public class MainGUI  {

    /**
     * 构造函数
     */ public MainGUI() {
        //初始化UI
        initComponents();
        //初始化数据
        initData();
    }


    public void initData(){
         //list填充
        autoRun.setSelected(BurpExtender.config.getAuto());
        ArrayList<String> arrayList = new ArrayList<>();
        for (Rule rule :BurpExtender.config.getList()) {
            arrayList.add(rule.getName());
        }
        watchList.setListData(arrayList.toArray(new String[0]));
        initTable();

    }

    private void initTable(){

        watchName.setText("");
        watchCustom.setText("");
        watchUrlInclude.setText("");
    }

    private int select = -1;

    /**
     * 显示错误信息
     */
    private void showMsg(String msg){
        JOptionPane.showMessageDialog(null, msg, "错误",
                JOptionPane.ERROR_MESSAGE);
    }
    private void watchSave(ActionEvent e) {

        if(watchName.getText().isEmpty()){
            showMsg("必须填写脚本名称");
            return;
        }
        if(watchCustom.getText().isEmpty()){
            showMsg("必须填写脚本执行命令");
            return;
        }
        Rule rule = new Rule(watchName.getText(),watchUrlInclude.getText(),watchCustom.getText());
        BurpExtender.config.add(rule);
        initData();
    }

    private void watchDel(ActionEvent e) {
        if(select!=-1){
            BurpExtender.config.del(select);
            select = -1;
            initData();
        }
    }

    /**
     * 获取根View
     */
    public JPanel getRoot(){
        return panel1;
    }

    private void autoRunStateChanged(ChangeEvent e) {
        BurpExtender.config.setAuto(autoRun.isSelected());
    }

    private void watchListValueChanged(ListSelectionEvent e) {
        select = watchList.getSelectedIndex();
        if (select == -1) return;

        Rule rule = BurpExtender.config.getList().get(select);
        if(rule==null)return;
        initTable();
        watchName.setText(rule.getName());
        watchCustom.setText(rule.getCommand());
        watchUrlInclude.setText(rule.getUrl());
    }






    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        panel1 = new JPanel();
        panel7 = new JPanel();
        autoRun = new JCheckBox();
        splitPane1 = new JSplitPane();
        watchList = new JList<>();
        panel2 = new JPanel();
        panel3 = new JPanel();
        watchUrlInclude = new JTextField();
        label2 = new JLabel();
        panel4 = new JPanel();
        watchCustom = new JTextField();
        label8 = new JLabel();
        label7 = new JLabel();
        watchName = new JTextField();
        watchSave = new JButton();
        watchDel = new JButton();
        label1 = new JLabel();

        //======== panel1 ========
        {
            panel1.setLayout(new BorderLayout());

            //======== panel7 ========
            {
                panel7.setBorder(new TitledBorder("\u63d2\u4ef6\u914d\u7f6e"));

                //---- autoRun ----
                autoRun.setText("\u81ea\u52a8\u6267\u884c\u811a\u672c");
                autoRun.addChangeListener(e -> autoRunStateChanged(e));

                GroupLayout panel7Layout = new GroupLayout(panel7);
                panel7.setLayout(panel7Layout);
                panel7Layout.setHorizontalGroup(
                    panel7Layout.createParallelGroup()
                        .addGroup(panel7Layout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(autoRun, GroupLayout.PREFERRED_SIZE, 800, GroupLayout.PREFERRED_SIZE)
                            .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                );
                panel7Layout.setVerticalGroup(
                    panel7Layout.createParallelGroup()
                        .addGroup(panel7Layout.createSequentialGroup()
                            .addComponent(autoRun, GroupLayout.PREFERRED_SIZE, 36, GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, Short.MAX_VALUE))
                );
            }
            panel1.add(panel7, BorderLayout.NORTH);

            //======== splitPane1 ========
            {
                splitPane1.setDividerLocation(200);

                //---- watchList ----
                watchList.setMaximumSize(new Dimension(200, 62));
                watchList.setFixedCellWidth(200);
                watchList.setBorder(LineBorder.createBlackLineBorder());
                watchList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                watchList.addListSelectionListener(e -> watchListValueChanged(e));
                splitPane1.setLeftComponent(watchList);

                //======== panel2 ========
                {
                    panel2.setBorder(new EmptyBorder(20, 20, 20, 20));

                    //======== panel3 ========
                    {
                        panel3.setBorder(new TitledBorder("\u76d1\u63a7\u53c2\u6570\uff08\u81ea\u52a8\u6267\u884c\u811a\u672c\u9700\u8981\u914d\u7f6e\uff09"));

                        //---- label2 ----
                        label2.setText("URL\u5305\u542b");

                        GroupLayout panel3Layout = new GroupLayout(panel3);
                        panel3.setLayout(panel3Layout);
                        panel3Layout.setHorizontalGroup(
                            panel3Layout.createParallelGroup()
                                .addGroup(panel3Layout.createSequentialGroup()
                                    .addGap(11, 11, 11)
                                    .addComponent(label2)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(watchUrlInclude, GroupLayout.DEFAULT_SIZE, 699, Short.MAX_VALUE)
                                    .addContainerGap())
                        );
                        panel3Layout.setVerticalGroup(
                            panel3Layout.createParallelGroup()
                                .addGroup(GroupLayout.Alignment.TRAILING, panel3Layout.createSequentialGroup()
                                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(panel3Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(label2)
                                        .addComponent(watchUrlInclude, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addGap(128, 128, 128))
                        );
                    }

                    //======== panel4 ========
                    {
                        panel4.setBorder(new TitledBorder("\u811a\u672c\u914d\u7f6e"));

                        //---- label8 ----
                        label8.setText("\u6267\u884c\u547d\u4ee4\uff08\u53ef\u6267\u884c\u7a0b\u5e8f\u5b8c\u6574\u8def\u5f84\uff09\uff1a");

                        //---- label7 ----
                        label7.setText("\u914d\u7f6e\u540d\u79f0\uff1a");

                        GroupLayout panel4Layout = new GroupLayout(panel4);
                        panel4.setLayout(panel4Layout);
                        panel4Layout.setHorizontalGroup(
                            panel4Layout.createParallelGroup()
                                .addGroup(panel4Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(panel4Layout.createParallelGroup()
                                        .addGroup(panel4Layout.createSequentialGroup()
                                            .addComponent(label8)
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(watchCustom, GroupLayout.DEFAULT_SIZE, 553, Short.MAX_VALUE))
                                        .addGroup(panel4Layout.createSequentialGroup()
                                            .addComponent(label7)
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(watchName)))
                                    .addContainerGap())
                        );
                        panel4Layout.setVerticalGroup(
                            panel4Layout.createParallelGroup()
                                .addGroup(panel4Layout.createSequentialGroup()
                                    .addGap(8, 8, 8)
                                    .addGroup(panel4Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(label7)
                                        .addComponent(watchName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addGroup(panel4Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(label8)
                                        .addComponent(watchCustom, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addGap(0, 0, Short.MAX_VALUE))
                        );
                    }

                    //---- watchSave ----
                    watchSave.setText("\u4fdd\u5b58");
                    watchSave.addActionListener(e -> watchSave(e));

                    //---- watchDel ----
                    watchDel.setText("\u5220\u9664");
                    watchDel.addActionListener(e -> watchDel(e));

                    GroupLayout panel2Layout = new GroupLayout(panel2);
                    panel2.setLayout(panel2Layout);
                    panel2Layout.setHorizontalGroup(
                        panel2Layout.createParallelGroup()
                            .addGroup(panel2Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(watchDel)
                                .addGap(18, 18, 18)
                                .addComponent(watchSave)
                                .addContainerGap(627, Short.MAX_VALUE))
                            .addGroup(panel2Layout.createSequentialGroup()
                                .addGroup(panel2Layout.createParallelGroup()
                                    .addComponent(panel4, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(panel3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap())
                    );
                    panel2Layout.setVerticalGroup(
                        panel2Layout.createParallelGroup()
                            .addGroup(panel2Layout.createSequentialGroup()
                                .addComponent(panel4, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(panel3, GroupLayout.PREFERRED_SIZE, 74, GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addGroup(panel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    .addComponent(watchDel)
                                    .addComponent(watchSave, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap(171, Short.MAX_VALUE))
                    );
                }
                splitPane1.setRightComponent(panel2);
            }
            panel1.add(splitPane1, BorderLayout.CENTER);

            //---- label1 ----
            label1.setText("    \u795e\u8bf4\uff1a\u8981\u89e3\u5bc6\uff0c\u4e8e\u662f\u5c31\u6709\u4e86iCrypto\u3002Powered by Ankio");
            panel1.add(label1, BorderLayout.SOUTH);
        }
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }


    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel panel1;
    private JPanel panel7;
    private JCheckBox autoRun;
    private JSplitPane splitPane1;
    private JList<String> watchList;
    private JPanel panel2;
    private JPanel panel3;
    private JTextField watchUrlInclude;
    private JLabel label2;
    private JPanel panel4;
    private JTextField watchCustom;
    private JLabel label8;
    private JLabel label7;
    private JTextField watchName;
    private JButton watchSave;
    private JButton watchDel;
    private JLabel label1;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
