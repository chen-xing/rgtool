package net.gzcx.net;

import cn.hutool.core.util.RuntimeUtil;
import net.gzcx.utils.SystemUtil;
import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI;

import javax.swing.*;
import java.awt.*;

/**
 * @author chen xing
 * @description 网络小助手
 * @date 2023-05-05 14:13
 */
public class NetAssistant extends JPanel {

    private JButton refreshNetSimpleInfoBtn = new JButton("刷新简要网络信息");
    private JButton refreshNetInfoBtn = new JButton("刷新网络信息");
    private JButton refreshDNSBtn = new JButton("刷新DNS缓存");

    private JTextArea txtResult = new JTextArea(); // 输出执行的结果

    public NetAssistant() {
        super(new BorderLayout());
        initGUI();
        initListeners();
        // 默认展示网络信息
        refreshNetSimpleInfo();
    }

    private void initGUI() {
        // init sub coms
        txtResult.setLineWrap(true);

        refreshNetSimpleInfoBtn.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.red));
        refreshNetSimpleInfoBtn.setForeground(Color.white);

        refreshNetInfoBtn.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.green));
        refreshNetInfoBtn.setForeground(Color.white);

        refreshDNSBtn.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.blue));
        refreshDNSBtn.setForeground(Color.white);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.add(refreshNetSimpleInfoBtn);
        btnPanel.add(refreshNetInfoBtn);
        btnPanel.add(refreshDNSBtn);

        this.add(btnPanel, BorderLayout.NORTH);
        this.add(new JScrollPane(txtResult), BorderLayout.CENTER);
        this.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
    }

    private void initListeners() {
        refreshNetSimpleInfoBtn.addActionListener(
                e -> {
                    refreshNetSimpleInfo();
                });

        refreshNetInfoBtn.addActionListener(
                e -> {
                    refreshNetInfo();
                });

        refreshDNSBtn.addActionListener(
                e -> {
                    refrshDNS();
                });
    }

    /**
     * @author chen xing
     * @description 重新获取网络基本信息
     * @param
     * @return void
     * @date 2023-05-05 14:25
     */
    private void refreshNetSimpleInfo() {
        String result = null;
        if (SystemUtil.isWindowsOs()) {
            result = RuntimeUtil.execForStr("ipconfig");
        } else {
            result = RuntimeUtil.execForStr("ifconfig");
        }
        txtResult.setText(result);
        txtResult.setCaretPosition(0);
    }

    /**
     * @author chen xing
     * @description 重新获取网络信息
     * @param
     * @return void
     * @date 2023-05-05 14:25
     */
    private void refreshNetInfo() {
        String result = null;
        if (SystemUtil.isWindowsOs()) {
            result = RuntimeUtil.execForStr("ipconfig /all");
        } else {
            result = RuntimeUtil.execForStr("netstat -nat");
        }
        txtResult.setText(result);
        txtResult.setCaretPosition(0);
    }

    private void refrshDNS() {
        String result = null;
        if (SystemUtil.isWindowsOs()) {
            result = RuntimeUtil.execForStr("ipconfig /flushdns");
        } else {
            result = RuntimeUtil.execForStr("killall -HUP mDNSResponder");
        }
        txtResult.setText(result);
        txtResult.setCaretPosition(0);
    }
}
