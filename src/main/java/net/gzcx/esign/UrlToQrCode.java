package net.gzcx.esign;

import com.github.pagehelper.util.StringUtil;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.sun.awt.AWTUtilities;
import lombok.extern.slf4j.Slf4j;
import net.gzcx.photoframe.NPIconFactory;
import net.gzcx.utils.NPComponentUtils;
import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI;
import net.gzcx.photoframe.Demo;
import net.gzcx.utils.DragToMove;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * 功能说明：
 *
 * @author fengqingyang
 * @return
 * @date [2020年06月06日上午22:23]
 */
@Slf4j
public class UrlToQrCode extends JPanel {
    private JDialog dialogForShowingPhoto = null;
    private JPanel panePhotoframe = null;
    private JTextArea txtUrl = new JTextArea();
    private JButton btnShowQrcode = null;
    private JButton btnShowInFrame = null;
    private JButton btnHideTheFrame = null;
    private JPanel centerPanel = null;

    public UrlToQrCode() {
        super(new BorderLayout());

        initGUI();
        initListeners();
    }

    private void initGUI() {
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        // init components
        txtUrl.setLineWrap(true);

        btnShowQrcode = new JButton("get qrcode");
        btnShowQrcode.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.green));
        btnShowQrcode.setForeground(Color.white);

        btnShowInFrame = new JButton("Show in new frame...");
        btnShowInFrame.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.blue));
        btnShowInFrame.setForeground(Color.white);

        btnHideTheFrame = new JButton("Hide the frame");
        btnHideTheFrame.setEnabled(false);

        JPanel paneBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        paneBtn.add(btnShowQrcode);
        paneBtn.add(btnShowInFrame);
        paneBtn.add(btnHideTheFrame);

        panePhotoframe = createPhotoframe();
        panePhotoframe.add(
                new JLabel(new ImageIcon(Demo.class.getResource("/imgs/girl.png"))),
                BorderLayout.CENTER);

        centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(1, 2, 10, 10));
        centerPanel.add(new JScrollPane(txtUrl));
        centerPanel.add(panePhotoframe);
        this.add(centerPanel, BorderLayout.CENTER);
        this.add(paneBtn, BorderLayout.NORTH);

        // drag panePhotoframe to move its parent window
        DragToMove.apply(new Component[] {panePhotoframe});
    }

    private void initListeners() {
        btnShowQrcode.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String url = txtUrl.getText();
                        if (StringUtil.isEmpty(url)) {
                            JOptionPane.showMessageDialog(
                                    null, "url不能为空", "提醒", JOptionPane.INFORMATION_MESSAGE);
                            return;
                        }
                        Image image = getQrcodeByUrl(url);

                        ((JLabel) (panePhotoframe.getComponent(0))).setIcon(new ImageIcon(image));
                    }
                });

        btnShowInFrame.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        showNewFrame();
                    }
                });
        btnHideTheFrame.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        hideTheFrame();
                    }
                });
    }

    /**
     * Create and return a new photo frame pane object. Its background is NinePatch pictrue.
     *
     * @return
     */
    private JPanel createPhotoframe() {
        //		JPanel pf = new JPanel();// TODO !!!!!!!!!!!!!!!!!!!!!!!!!!!
        JPanel pf =
                NPComponentUtils.createPanel_root(
                        NPIconFactory.getInstance().getPhotoframeBg(), new Insets(13, 15, 15, 15));
        pf.setLayout(new BorderLayout());
        pf.setOpaque(false);

        //		pf.setBorder(new AABorder());
        return pf;
    }

    public void showNewFrame() {
        if (dialogForShowingPhoto == null) {
            dialogForShowingPhoto =
                    new JDialog(
                            // bug of JDK1.7: can't repaint!
                            //
                            //	SwingUtilities.getWindowAncestor(org.jb2011.ninepatch4j.demos.photoframe.Demo.this)
                            );
            // set dialog full transparent
            dialogForShowingPhoto.setUndecorated(true);
            AWTUtilities.setWindowOpaque(dialogForShowingPhoto, false);
            // contentPane default is opaque in Java1.7+
            ((JComponent) (dialogForShowingPhoto.getContentPane())).setOpaque(false);
            dialogForShowingPhoto.getRootPane().setWindowDecorationStyle(JRootPane.NONE);
            dialogForShowingPhoto.setLocation(100, 100);
            //			dialogForShowingPhoto.setLocationRelativeTo(null);
            dialogForShowingPhoto.setAlwaysOnTop(true); //
        }

        dialogForShowingPhoto.setSize(500, 500);
        //		this.remove(panePhotoframe);
        dialogForShowingPhoto.add(panePhotoframe);

        dialogForShowingPhoto.setVisible(true);
        btnHideTheFrame.setEnabled(true);
        btnShowInFrame.setEnabled(false);
    }

    public void hideTheFrame() {
        dialogForShowingPhoto.setVisible(false);
        btnHideTheFrame.setEnabled(false);
        btnShowInFrame.setEnabled(true);

        //		dialogForShowingPhoto.remove(panePhotoframe);
        // add to Demo main pane again
        centerPanel.add(panePhotoframe);
        centerPanel.revalidate();
        //        this.add(panePhotoframe, BorderLayout.CENTER);
    }

    /**
     * 获取url对应的二维码
     *
     * @param url
     * @return
     */
    private Image getQrcodeByUrl(String url) {
        Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 4);
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        BitMatrix bitMatrix = null; // 生成矩阵
        try {
            bitMatrix = new MultiFormatWriter().encode(url, BarcodeFormat.QR_CODE, 400, 400, hints);
        } catch (WriterException e) {
            log.error("create qrcode error:{}", e.getMessage());
        }
        BufferedImage bi = MatrixToImageWriter.toBufferedImage(bitMatrix);
        return bi;
    }
}
