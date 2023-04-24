package net.gzcx.tool.image;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.swing.clipboard.ClipboardUtil;
import cn.hutool.http.HttpUtil;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI;
import net.gzcx.utils.SystemUtil;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class ImageHandler extends JPanel {
    private JButton btnGetFromClipboard = new JButton("从剪切板获取");
    private JButton btnGenerareUrl = new JButton("生成链接");
    private JButton btnCopyUrl = new JButton("复制链接");
    private JTextArea JTextUrl = new JTextArea();
    private JLabel imageLable = new JLabel();
    public final String IMAGE_PATH_PRE_FIX =
            SystemUtil.configHome + File.separator + "images" + File.separator;
    private Image tempimage = null;

    public ImageHandler() {
        super(new BorderLayout());
        initGUI();
        initListeners();
    }

    private void initGUI() {
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        btnGetFromClipboard.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.red));
        btnGetFromClipboard.setForeground(Color.white);
        btnGetFromClipboard.setToolTipText("Ctrl+V");

        btnGenerareUrl.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.lightBlue));
        btnGenerareUrl.setForeground(Color.white);

        btnCopyUrl.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.green));
        btnCopyUrl.setForeground(Color.white);

        JPanel btnPane = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnPane.add(btnGetFromClipboard);
        btnPane.add(btnGenerareUrl);
        btnPane.add(btnCopyUrl);
        btnPane.add(JTextUrl);
        this.add(btnPane, BorderLayout.NORTH);

        JScrollPane jScrollPane = new JScrollPane();
        imageLable.setText("");
        imageLable.setIcon(new ImageIcon(this.getClass().getResource("/imgs/girl.png")));
        jScrollPane.setViewportView(imageLable);
        jScrollPane.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.lightGray));
        this.add(jScrollPane, BorderLayout.CENTER);
    }

    private void initListeners() {
        btnGetFromClipboard.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        getImageFromClipboard();
                    }
                });

        btnGenerareUrl.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        genareUrl();
                    }
                });

        btnCopyUrl.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        copyToClipboard();
                    }
                });
    }

    private void copyToClipboard() {
        try {
            ClipboardUtil.setStr(this.JTextUrl.getText());
            JOptionPane.showMessageDialog(this, "已复制链接到剪贴板！");
        } catch (Exception e1) {
            JOptionPane.showMessageDialog(
                    this, "复制失败！\n\n" + e1.getMessage(), "失败", JOptionPane.ERROR_MESSAGE);
            log.error("{}", e1.getMessage());
        }
    }

    public void getImageFromClipboard() {
        try {
            Image image = ClipboardUtil.getImage();
            tempimage = image;

            if (image != null) {
                imageLable.setIcon(new ImageIcon(image));
            } else {
                JOptionPane.showMessageDialog(
                        this, "还没有复制图片到剪贴板吧？\n\n", "失败", JOptionPane.WARNING_MESSAGE);
            }
        } catch (HeadlessException ex) {
            log.error("{}", ex.getMessage());
        }
    }

    private String genareUrl() {
        if (null == tempimage) {
            JOptionPane.showMessageDialog(this, "无找到对应图片\n\n", "失败", JOptionPane.WARNING_MESSAGE);
            return null;
        } else {
            try {
                File imageFile =
                        FileUtil.touch(
                                new File(
                                        IMAGE_PATH_PRE_FIX
                                                + UUID.randomUUID().toString()
                                                + ".png"));
                ImageIO.write(toBufferedImage(tempimage), "png", imageFile);
                String url = getUrl(imageFile);
                this.JTextUrl.setText(url);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        this, "保存失败！\n\n" + ex.getMessage(), "失败", JOptionPane.ERROR_MESSAGE);
                log.error("{}", ex.getMessage());
            }
        }
        return null;
    }

    private BufferedImage toBufferedImage(Image image) {
        BufferedImage bufferedImage =
                new BufferedImage(
                        image.getWidth((ImageObserver) null),
                        image.getHeight((ImageObserver) null),
                        2);
        Graphics2D g = bufferedImage.createGraphics();
        g.drawImage(image, 0, 0, (ImageObserver) null);
        g.dispose();
        return bufferedImage;
    }

    private String getUrl(File imageFile) {
        HashMap<String, Object> paramMap = new HashMap<>();
        // 文件上传只需将参数中的键指定（默认file），值设为文件对象即可，对于使用者来说，文件上传与普通表单提交并无区别
        paramMap.put("Filedata", imageFile);
        String result = HttpUtil.post("https://www.chenzhuofan.top/webapi/images", paramMap);
        Map<String, String> map = new Gson().fromJson(result, Map.class);
        if (null == map || map.isEmpty()) {
            return null;
        }
        return map.get("data");
    }
}
