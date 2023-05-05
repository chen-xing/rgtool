/*
 * Copyright (C) 2015 Jack Jiang(cngeeker.com) The Swing9patch Project.
 * All rights reserved.
 * Project URL:https://github.com/JackJiang2011/Swing9patch
 * Version 1.0
 *
 * Jack Jiang PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * MainFrame.java at 2015-2-6 16:10:04, original version by Jack Jiang.
 * You can contact author with jb2011@163.com.
 */
package net.gzcx;

import net.gzcx.esign.DecryptContext;
import net.gzcx.esign.SmallTool;
import net.gzcx.esign.UrlToQrCode;
import net.gzcx.esign.file.FileUpload;
import net.gzcx.net.NetAssistant;
import net.gzcx.tool.Calculator;
import net.gzcx.utils.ComponentUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame {
    private JPanel mainPane = new JPanel(new BorderLayout());

    public MainFrame() throws HeadlessException {
        super("人工工具箱 - 欢迎关注程序员导航网：https://www.gzcx.net");
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Image image = new ImageIcon(classLoader.getResource("imgs/mickey.png")).getImage();
        this.setIconImage(image);
        ComponentUtil.setPreferSizeAndLocateToCenter(this, 0.8, 0.88);
        initGUI();
    }

    private void initGUI() {
        this.mainPane.add(createMainTabs(), BorderLayout.CENTER);

        this.getContentPane().add(mainPane);
        this.setJMenuBar(createMenuBar());
    }

    private JComponent createMainTabs() {
        JTabbedPane tbs = new JTabbedPane();

        tbs.add(new DecryptContext(), "签署链接处理");
        tbs.add(new UrlToQrCode(), "链接转二维码");
        tbs.add(new SmallTool(), "小工具");
        tbs.add(new FileUpload(), "文件上传");
        tbs.add(new NetAssistant(), "网络信息");
        tbs.add(new Calculator(), "计算器");


        //        tbs.add(new ImageHandler(), "图床工具");
        //        tbs.add(new FileSearch(), "文件检索");
        //        tbs.add(new SeoAssistant(), "SEO助手");
        //  tbs.add(new DataTableView(), "DataTableView");

        tbs.setToolTipTextAt(0, "dncrypt context");
        tbs.setToolTipTextAt(1, "urltoQrcode");
        tbs.setToolTipTextAt(2, "small tool");
        tbs.setToolTipTextAt(3, "file upload");
        tbs.setToolTipTextAt(4, "net ip info");
        tbs.setToolTipTextAt(5, "practical computer");
        //        tbs.setToolTipTextAt(3, "image tool");
        //        tbs.setToolTipTextAt(4, "file search");
        //        tbs.setToolTipTextAt(6, "Seo Assistant");
        // tbs.setToolTipTextAt(5, "DataTableView");
        return tbs;
    }

    private JMenuBar createMenuBar() {
        // ------------------------------------ MenuDemo1
        //		JMenu fileMenu = new JMenu("MenuDemo1");
        //		JMenuItem openMenuItem = new JMenuItem("Menu item 1");
        //		JMenuItem saveMenuItem = new JMenuItem("Menu item 2");
        //		JMenuItem exitMenuItem = new JMenuItem("Menu item 3");
        //		fileMenu.add(openMenuItem);
        //		saveMenuItem.setEnabled(false);
        //		fileMenu.add(saveMenuItem);
        //		fileMenu.addSeparator();
        //		fileMenu.add(exitMenuItem);

        // ------------------------------------ MenuDemo2
        //		JMenu fileMenu2 = new JMenu("MenuDemo2");
        //		fileMenu2.add(new JMenuItem("Menu item 1"));
        //		fileMenu2.add(new JMenuItem("Menu item 2"));
        //		fileMenu2.addSeparator();
        //		fileMenu2.add(new JMenuItem("Menu item 3"));
        //		fileMenu2.add(new JMenuItem("Menu item 4"));
        //		fileMenu2.addSeparator();
        //		fileMenu2.add(new JMenuItem("Menu item 5"));
        //		fileMenu2.add(new JMenuItem("Menu item 6"));
        //		fileMenu2.add(new JMenuItem("Menu item 7"));
        //		fileMenu2.add(new JMenuItem("Menu item 8"));

        // ------------------------------------ About
        JMenu aboutMenu = new JMenu("关于");
        JMenuItem aboutMenuItem = new JMenuItem("关于本工程");
        aboutMenuItem.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        JOptionPane.showMessageDialog(
                                rootPane,
                                "rgtool"
                                        + "\n - 本工程是一组提供工作效率的工具的集合."
                                        + "\n - 作者 chen xing (https://www.gzcx.net)."
                                        + "\n");
                    }
                });
        aboutMenu.add(aboutMenuItem);

        JMenuBar menuBar = new JMenuBar();
        //		menuBar.add(fileMenu);
        //		menuBar.add(fileMenu2);
        menuBar.add(aboutMenu);

        return menuBar;
    }

    public static MainFrame getInstance() {
        return new MainFrame();
    }
}
