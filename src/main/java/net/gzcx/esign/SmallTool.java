package net.gzcx.esign;

import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import net.gzcx.fixtip.FixtipPane;
import net.gzcx.fixtip.FloatableDialog;
import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 功能说明：
 *
 * @author fengqingyang
 * @return
 * @date [2020年06月08日上午20:50]
 */
@Slf4j
public class SmallTool extends JPanel {
    private JTextArea txtMsg = new JTextArea();
    private JButton btnGetUUID = new JButton("UUID生成器");
    private JButton btnGetCurrentTimestamp = new JButton("获取当前时间戳");
    private JButton btnTimestampToDate = new JButton("时间戳转时间");
    private JButton btnDateToTimestamp = new JButton("日期转时间戳");
    private JButton btnUrlEncode = new JButton("加密");
    private JButton btnUrlDecode = new JButton("解密");
    private FixtipPane fixtipPane = new FixtipPane();
    private FloatableDialog fixtipDialog = null;

    public SmallTool() {
        super(new BorderLayout());

        initGUI();
        initListeners();
    }

    private void initGUI() {
        // init sub coms
        txtMsg.setText("please input here......");
        txtMsg.setLineWrap(true);

        btnGetUUID.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.blue));
        btnGetUUID.setForeground(Color.white);

        btnGetCurrentTimestamp.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.red));
        btnGetCurrentTimestamp.setForeground(Color.white);

        btnTimestampToDate.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.green));
        btnTimestampToDate.setForeground(Color.white);

        btnDateToTimestamp.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.blue));
        btnDateToTimestamp.setForeground(Color.white);

        btnUrlEncode.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.lightBlue));
        btnUrlEncode.setForeground(Color.white);

        btnUrlDecode.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.red));
        btnUrlDecode.setForeground(Color.white);

        // init btn pane
        JPanel btnPane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPane.add(btnGetUUID);
        btnPane.add(btnGetCurrentTimestamp);
        btnPane.add(btnTimestampToDate);
        btnPane.add(btnDateToTimestamp);
        btnPane.add(btnUrlEncode);
        btnPane.add(btnUrlDecode);

        // init main ui
        this.add(btnPane, BorderLayout.NORTH);
        this.add(new JScrollPane(txtMsg), BorderLayout.CENTER);
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }

    private void initListeners() {
        btnGetUUID.addActionListener(
                x -> {
                    String randomUUID = IdUtil.randomUUID();
                    txtMsg.setText(randomUUID);
                });

        btnGetCurrentTimestamp.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String currentTimestamp = String.valueOf(System.currentTimeMillis());
                        txtMsg.setText(currentTimestamp);
                    }
                });

        btnTimestampToDate.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String timestampStr = txtMsg.getText();
                        SimpleDateFormat simpleDateFormat =
                                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                        String dateStr =
                                simpleDateFormat.format(
                                        new Date(Long.valueOf(Long.parseLong(timestampStr))));
                        txtMsg.setText(dateStr);
                    }
                });

        btnDateToTimestamp.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String dateStr = txtMsg.getText();
                        SimpleDateFormat simpleDateFormat =
                                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

                        try {
                            Date date = simpleDateFormat.parse(dateStr);
                            String timestamp = String.valueOf(date.getTime());
                            txtMsg.setText(timestamp);
                        } catch (ParseException ex) {
                            log.error("parse date error:{}", ex.getMessage());
                            JOptionPane.showMessageDialog(
                                    null, "格式有误", "提醒", JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                });

        btnUrlEncode.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String originSrc = txtMsg.getText();
                        if (null == originSrc || originSrc.length() == 0) {
                            JOptionPane.showMessageDialog(
                                    null, "格式有误", "提醒", JOptionPane.INFORMATION_MESSAGE);
                        }
                        originSrc = URLEncoder.encode(originSrc);
                        txtMsg.setText(originSrc);

                        fixtipPane.setTiptext(txtMsg.getText());
                        if (fixtipDialog == null) {
                            fixtipDialog =
                                    FloatableDialog.createDialog(
                                            fixtipPane,
                                            SwingUtilities.getWindowAncestor(SmallTool.this),
                                            txtMsg);
                            fixtipDialog.setInvisibleOnDispose(true);
                        }

                        fixtipDialog.display();
                    }
                });

        btnUrlDecode.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String originSrc = txtMsg.getText();
                        if (null == originSrc || originSrc.length() == 0) {
                            JOptionPane.showMessageDialog(
                                    null, "格式有误", "提醒", JOptionPane.INFORMATION_MESSAGE);
                        }
                        try {
                            originSrc = URLDecoder.decode(originSrc, "utf-8");
                        } catch (UnsupportedEncodingException ex) {
                            txtMsg.setText(ex.getMessage());
                        }
                        txtMsg.setText(originSrc);

                        fixtipPane.setTiptext(txtMsg.getText());
                        if (fixtipDialog == null) {
                            fixtipDialog =
                                    FloatableDialog.createDialog(
                                            fixtipPane,
                                            SwingUtilities.getWindowAncestor(SmallTool.this),
                                            txtMsg);
                            fixtipDialog.setInvisibleOnDispose(true);
                        }

                        fixtipDialog.display();
                    }
                });
    }
}
