package net.gzcx.esign;

import cn.hutool.json.JSONUtil;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.timevale.esign.compontent.simple.encrypt.SimpleCipher;
import net.gzcx.fixtip.FixtipPane;
import net.gzcx.fixtip.FloatableDialog;
import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;

/**
 * 功能说明：
 *
 * @author fengqingyang
 * @return
 * @date [2020年06月05日上午22:23]
 */
public class DecryptContext extends JPanel {
    private JTextArea txtMsg = new JTextArea();
    private JButton btnDecrypt = new JButton("解密");
    private JCheckBox checkbox = new JCheckBox("urldecode", true);
    private JCheckBox checkboxToJson = new JCheckBox("jsonresult", true);

    private JButton btnEncrypt = new JButton("加密");
    private FixtipPane fixtipPane = new FixtipPane();
    private FloatableDialog fixtipDialog = null;

    private JComboBox jComboBox = new JComboBox<>();

    public DecryptContext() {
        super(new BorderLayout());
        initGUI();
        initListeners();
    }

    private void initGUI() {
        // init sub coms
        txtMsg.setLineWrap(true);
        btnDecrypt.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.red));
        btnDecrypt.setForeground(Color.white);

        btnEncrypt.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.green));
        btnEncrypt.setForeground(Color.white);

        // init btn pane
        JPanel btnPane = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JPanel evnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        evnPanel.setBorder(BorderFactory.createTitledBorder("环境设置"));
        jComboBox.addItem("测试");
        jComboBox.addItem("模拟");
        jComboBox.addItem("生成");
        evnPanel.add(jComboBox);
        btnPane.add(evnPanel);

        JPanel leftBtnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        leftBtnPanel.setBorder(BorderFactory.createTitledBorder("内容提取"));
        leftBtnPanel.add(checkbox);
        leftBtnPanel.add(checkboxToJson);
        leftBtnPanel.add(btnDecrypt);

        btnPane.add(leftBtnPanel);

        JPanel rightBtnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        rightBtnPanel.setBorder(BorderFactory.createTitledBorder("生成链接"));
        rightBtnPanel.add(btnEncrypt);
        btnPane.add(rightBtnPanel);

        // init main ui
        this.add(btnPane, BorderLayout.NORTH);
        this.add(new JScrollPane(txtMsg), BorderLayout.CENTER);
        this.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
    }

    private void initListeners() {
        btnDecrypt.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String originSrc = txtMsg.getText();
                        if (null == originSrc || originSrc.length() == 0) {
                            txtMsg.setText("原始字符串不能为空");
                        }
                        boolean urlDecode = checkbox.isSelected();
                        boolean resultjson = checkboxToJson.isSelected();
                        if (urlDecode) {
                            originSrc = URLDecoder.decode(originSrc);
                        }
                        try {
                            String decParam =
                                    SimpleCipher.INSTANCE.decode("AES", originSrc, "UTF-8");
                            if (resultjson) {
                                decParam = urlToJson(decParam);
                                decParam = JSONUtil.toJsonPrettyStr(decParam);
                            }
                            txtMsg.setText(decParam);
                        } catch (Exception ex) {
                            txtMsg.setText(ex.getMessage());
                        }
                        fixtipPane.setTiptext(txtMsg.getText());
                        if (fixtipDialog == null) {
                            fixtipDialog =
                                    FloatableDialog.createDialog(
                                            fixtipPane,
                                            SwingUtilities.getWindowAncestor(DecryptContext.this),
                                            txtMsg);
                            fixtipDialog.setInvisibleOnDispose(true);
                        }

                        fixtipDialog.display();
                    }
                });

        btnEncrypt.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String originSre = txtMsg.getText();
                        if (null == originSre || originSre.length() == 0) {
                            txtMsg.setText("原始字符串不能为空");
                        }

                        boolean urlDecode = checkbox.isSelected();
                        try {
                            String encryParam =
                                    SimpleCipher.INSTANCE.encode("AES", originSre, "UTF-8");
                            if (urlDecode) {
                                encryParam = URLEncoder.encode(encryParam);
                            }
                            txtMsg.setText(encryParam);
                        } catch (Exception ex) {
                            txtMsg.setText(ex.getMessage());
                        }
                        fixtipPane.setTiptext(txtMsg.getText());
                        if (fixtipDialog == null) {
                            fixtipDialog =
                                    FloatableDialog.createDialog(
                                            fixtipPane,
                                            SwingUtilities.getWindowAncestor(DecryptContext.this),
                                            txtMsg);
                            fixtipDialog.setInvisibleOnDispose(true);
                        }

                        fixtipDialog.display();
                    }
                });
    }

    /**
     * url转json
     *
     * @param url
     * @return
     */
    private String urlToJson(String url) {
        String[] keyPairArr = url.split("&");
        if (null == keyPairArr || keyPairArr.length == 0) {
            return null;
        }
        Map<String, Object> map = Maps.newConcurrentMap();
        for (String str : keyPairArr) {
            String[] keyArr = str.split("=");
            if (null != keyArr && keyArr.length == 2) {
                map.put(keyArr[0], keyArr[1]);
            }
        }
        if (map.isEmpty()) {
            return null;
        }
        return new Gson().toJson(map);
    }
}
