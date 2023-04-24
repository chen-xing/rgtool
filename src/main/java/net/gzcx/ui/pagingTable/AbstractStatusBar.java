/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.gzcx.ui.pagingTable;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 分页状态条 用法如下： class StatusBar extends AbstractStatusBar { @Override public String getSql() { //
 * TODO Auto-generated method stub // 组装全部查询语句 String sql = "select * from ..."; return sql;
 * } @Override public void fillToTable(PageInfo pageInfo) { // TODO Auto-generated method stub
 * //得到当前页查询语句 String cursql = pageInfo.getCurQuerySQL(); //利用当前页查询语句cursql做你自己的事情，如：查询出的数据填充到table
 * List list=query(cursql); table.clear(); table.addAll(list); } }
 */
public abstract class AbstractStatusBar extends JPanel implements ActionListener {

    /** 每页显示多少条 */
    Integer[] pageSizeArr = new Integer[] {2, 10, 50, 100};
    //    Box statusBarLeft;
    //    JLabel actionStatus;
    JButton previous;
    JButton next;
    JButton first;
    JButton last;
    JLabel statusLabel;
    JComboBox pageSizeComb = null;
    JTextField pageNoText;
    JButton go;
    private static final int H_BUT_PAD = 5;
    private RgPageInfo pageInfo;

    public AbstractStatusBar() {
        this(new RgPageInfo());
    }

    public AbstractStatusBar(RgPageInfo pageInfo) {
        super();
        this.pageInfo = pageInfo;
        this.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0));
        this.setLayout(new BorderLayout());
        Box statusBox = Box.createHorizontalBox();

        // Left status area
        statusBox.add(Box.createRigidArea(new Dimension(10, 22)));
        previous = ButtonUtil.makeNavigationButton(null, "previous", "上一页", "上一页", this);
        next = ButtonUtil.makeNavigationButton(null, "next", "下一页", "下一页", this);
        first = ButtonUtil.makeNavigationButton(null, "first", "首页", "首页", this);
        last = ButtonUtil.makeNavigationButton(null, "last", "末页", "末页", this);

        pageSizeComb =
                new JComboBox(pageSizeArr) {

                    @Override
                    public Dimension getMaximumSize() {
                        return new Dimension(1000, getPreferredSize().height);
                    }
                };

        pageSizeComb.setActionCommand("changePageSize");
        pageSizeComb.setSelectedItem(new Integer(pageInfo.getPageSize()));
        pageSizeComb.addActionListener(this);

        pageNoText =
                new JTextField(10) {

                    @Override
                    protected Document createDefaultModel() {
                        return new IntDocument();
                    }

                    class IntDocument extends PlainDocument {

                        @Override
                        public void insertString(int offs, String str, AttributeSet a)
                                throws BadLocationException {
                            if (str == null) {
                                return;
                            }
                            str = str.replaceAll("[^\\d]", "");
                            super.insertString(offs, str, a);
                        }
                    }

                    @Override
                    public Dimension getMaximumSize() {
                        return new Dimension(1000, getPreferredSize().height);
                    }
                };
        pageNoText.setActionCommand("go");
        pageNoText.addActionListener(this);

        go = ButtonUtil.makeNavigationButton(null, "go", "GO", "GO", this);

        statusLabel =
                new JLabel(
                        "<html>找到"
                                + pageInfo.getTotalSize()
                                + "条记录, 显示 "
                                + pageInfo.getCurFirst()
                                + " 到 "
                                + pageInfo.getCurLast()
                                + " ,当前第&nbsp;"
                                + (pageInfo.getCurPage())
                                + "&nbsp;页 , 共&nbsp;"
                                + (pageInfo.getTotalPage())
                                + "&nbsp;页   </htmnl>    ");

        Box pageSizePanel = Box.createHorizontalBox();
        pageSizePanel.add(new JLabel(" 每页显示"));
        pageSizePanel.add(pageSizeComb);
        pageSizePanel.add(new JLabel("条记录 "));

        Box goPanel = Box.createHorizontalBox();
        goPanel.add(new JLabel("跳转到第"));
        goPanel.add(pageNoText);
        goPanel.add(new JLabel("页"));
        goPanel.add(go);

        statusBox.add(statusLabel);
        statusBox.add(Box.createHorizontalGlue());
        statusBox.add(first);
        statusBox.add(Box.createHorizontalStrut(H_BUT_PAD));
        statusBox.add(previous);
        statusBox.add(Box.createHorizontalStrut(H_BUT_PAD));
        statusBox.add(next);
        statusBox.add(Box.createHorizontalStrut(H_BUT_PAD));
        statusBox.add(last);
        statusBox.add(Box.createHorizontalStrut(H_BUT_PAD));
        statusBox.add(pageSizePanel);
        statusBox.add(goPanel);

        // </snip>
        this.add(statusBox);

        initButtonStatus();
    }
    /*
     * 设置按钮状态
     */

    private void initButtonStatus() {
        first.setEnabled(!pageInfo.isFirst());
        previous.setEnabled(!pageInfo.isFirst());
        next.setEnabled(!pageInfo.isLast());
        last.setEnabled(!pageInfo.isLast());
    }

    public final void loadData() {
        EventQueue.invokeLater(
                new Runnable() {
                    @Override
                    public void run() {
                        loading();
                    }
                });
    }

    /**
     * 开始查询加载 pageInfo.setQuerySQL(sql);
     * pageInfo.setTotalSize(BaseDAO.getInstance().count(pageInfo.getCountSQL()));
     * pageInfo.setCurSize(BaseDAO.getInstance().count(pageInfo.getCurCountSQL()));
     */
    private final void loading() {
        query();
    }

    /** 填充表格 可 重写 */
    public abstract void fillToTable(RgPageInfo pageInfo);
    /*
     * 查询
     */

    public final void query() {
        fillToTable(pageInfo);
        finished();
    }
    /*
     * 查询完成
     */

    private final void finished() {
        initButtonStatus();
        statusLabel.setText(
                "<html>找到"
                        + pageInfo.getTotalSize()
                        + "条记录, 显示 "
                        + pageInfo.getCurFirst()
                        + " 到 "
                        + pageInfo.getCurLast()
                        + " ,当前第&nbsp;"
                        + (pageInfo.getCurPage())
                        + "&nbsp;页       共&nbsp;"
                        + (pageInfo.getTotalPage())
                        + "&nbsp;页   </htmnl>   ");
        /*this.revalidate();
        this.validate();
        this.invalidate();
        this.updateUI();*/
    }

    public void setPageInfo(RgPageInfo pageInfo) {
        this.pageInfo = pageInfo;
    }

    public RgPageInfo getPageInfo() {
        return pageInfo;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {

        String command = ae.getActionCommand();
        if (command.equals("next")) {
            pageInfo.next();
        } else if (command.equals("previous")) {
            pageInfo.previous();
        } else if (command.equals("first")) {
            pageInfo.goFirst();
        } else if (command.equals("last")) {
            pageInfo.goLast();
        } else if (command.equals("go")) {
            if (this.pageNoText.getText() != null && !this.pageNoText.getText().trim().equals("")) {
                pageInfo.go(Integer.parseInt(this.pageNoText.getText().trim()));
                this.pageNoText.setText("");
            }

        } else if (command.equals("changePageSize")) {
            pageInfo.setPageSize(Integer.parseInt(this.pageSizeComb.getSelectedItem().toString()));
        }
        this.query();
    }
}
