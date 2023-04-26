package net.gzcx.esign.file;

import com.timevale.mandarin.base.util.StringUtils;
import net.gzcx.domain.file.GetSignUrlResultBean;
import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;

/**
 * @author chen xing
 * @description TODO
 * @date 2023-04-24 10:21
 */
public class FileUpload extends JPanel {

    /** 系统换行符 */
    private static String separator = System.getProperty("line.separator");

    private JComboBox uploadEnv = new JComboBox();
    private JButton uploadBtn = new JButton("文件上传");

    private JTextArea txtResult = new JTextArea(); // 输出执行的结果

    private JTextField keywordTextField = new JTextField();

    private JButton getDownloadUrlBtn = new JButton("获取文件下载地址");

    public FileUpload() {
        super(new BorderLayout());
        initGUI();
        initListeners();
    }

    private void initGUI() {
        keywordTextField.setEditable(true);
        keywordTextField.setPreferredSize(new Dimension(800, 30));
        // init sub coms
        txtResult.setLineWrap(true);
        uploadBtn.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.red));
        uploadBtn.setForeground(Color.white);

        getDownloadUrlBtn.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.green));
        getDownloadUrlBtn.setForeground(Color.white);

        JPanel downloadPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        downloadPanel.setBorder(BorderFactory.createTitledBorder("文件下载"));
        downloadPanel.add(keywordTextField);
        downloadPanel.add(getDownloadUrlBtn);

        // init btn pane
        JPanel btnPane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPane.setBorder(BorderFactory.createTitledBorder("文件上传"));

        uploadEnv.setPreferredSize(new Dimension(80, 30));
        uploadEnv.addItem("测试");
        uploadEnv.addItem("模拟");

        btnPane.add(uploadEnv);
        btnPane.add(uploadBtn);

        JPanel toolPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        toolPanel.add(downloadPanel, BorderLayout.WEST);
        toolPanel.add(btnPane, BorderLayout.EAST);

        // init main ui

        this.add(toolPanel, BorderLayout.NORTH);
        this.add(new JScrollPane(txtResult), BorderLayout.CENTER);
        this.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
    }

    private void initListeners() {
        uploadBtn.addActionListener(
                e -> {
                    JFileChooser chooser = new JFileChooser();
                    FileNameExtensionFilter filter =
                            new FileNameExtensionFilter(
                                    "pdf & doc & JPG & GIF Images",
                                    "jpg",
                                    "gif",
                                    "png",
                                    "pdf",
                                    "doc",
                                    "docx");
                    chooser.setFileFilter(filter);
                    int returnVal = chooser.showOpenDialog(this);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        File selectedFile = chooser.getSelectedFile();
                        Object selectedItem = uploadEnv.getSelectedItem();

                        StringBuffer stringBuffer = new StringBuffer();
                        String absolutePath = selectedFile.getAbsolutePath();
                        GetSignUrlResultBean uploadFileKeyAndUrl =
                                FileSupport.getUploadFileKeyAndUrl(
                                        selectedItem.toString(), absolutePath);
                        if (uploadFileKeyAndUrl.getErrCode().equals("0")) {
                            stringBuffer.append(
                                    String.format(
                                            "文件上传的fileKey是:%s", uploadFileKeyAndUrl.getFileKey()));
                            stringBuffer.append(separator);
                            stringBuffer.append(separator);
                            stringBuffer.append(
                                    String.format("文件上传的地址是:%s", uploadFileKeyAndUrl.getUrl()));
                            stringBuffer.append(separator);
                            stringBuffer.append(separator);
                            stringBuffer.append("开始执行文件的上传");
                            stringBuffer.append(separator);
                            stringBuffer.append(separator);
                            boolean result =
                                    FileSupport.uploadFile(
                                            uploadFileKeyAndUrl.getUrl(), absolutePath);
                            stringBuffer.append(result ? "文件上传成功" : "文件上传失败");
                        } else {
                            stringBuffer.append(uploadFileKeyAndUrl.getMsg());
                        }
                        txtResult.setText(stringBuffer.toString());
                    }
                });

        getDownloadUrlBtn.addActionListener(
                e -> {
                    String text = keywordTextField.getText();
                    if (StringUtils.isBlank(text)) {
                        txtResult.setText("下载的fileKey不能为空");
                        return;
                    }

                    Object selectedItem = uploadEnv.getSelectedItem();
                    String downloadUrlByFileKey =
                            FileSupport.getDownloadUrlByFileKey(
                                    selectedItem.toString(), text.trim());
                    if (StringUtils.isNotBlank(downloadUrlByFileKey)) {
                        StringBuffer stringBuffer = new StringBuffer();
                        stringBuffer.append(String.format("%s 对应的下载地址是:", text));
                        stringBuffer.append(separator);
                        stringBuffer.append(downloadUrlByFileKey);
                        txtResult.setText(stringBuffer.toString());
                    }
                });
    }
}
