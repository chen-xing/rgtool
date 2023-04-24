package net.gzcx.hbaseviewer;

import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Maps;
import net.gzcx.mapper.ISqlMapper;
import net.gzcx.ui.pagingTable.PagingTable;
import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI;
import net.gzcx.utils.MybatisUtil;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.text.MessageFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DataTableView extends JPanel {
    private JButton btnQuery = new JButton("执行");
    Box vBox = Box.createVerticalBox();
    private JTextArea jTextArea = new JTextArea(5, 1); // sql执行区
    private JPanel jPanelResult = null; // 执行的结果
    private JTree tree = null; // 左边的树节点

    public DataTableView() {
        super(new BorderLayout());
        initGUI();
        initListeners();
    }

    private void initGUI() {
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        String querySql = "select * from t_quick_note ";
        LinkedHashMap<String, String> cloumnMapping = Maps.newLinkedHashMap();
        cloumnMapping.put("id", "序号");
        cloumnMapping.put("name", "标题");
        cloumnMapping.put("content", "内容");
        cloumnMapping.put("create_time", "创建时间");
        cloumnMapping.put("modified_time", "修改时间");
        jPanelResult = getResultPanel(querySql, cloumnMapping);

        JPanel panleQuery = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panleQuery.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

        btnQuery.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.green));
        btnQuery.setForeground(Color.white);
        panleQuery.add(btnQuery);
        this.add(panleQuery, BorderLayout.NORTH);

        Box vBox = Box.createVerticalBox();
        vBox.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        vBox.add(jTextArea);
        vBox.add(jPanelResult);
        this.add(vBox, BorderLayout.CENTER);

        JPanel jPanelTree = getDbTree();
        this.add(jPanelTree, BorderLayout.WEST);
    }

    private void initListeners() {
        btnQuery.addActionListener(
                x -> {
                    String runSql = jTextArea.getText();
                    if (StrUtil.isBlank(runSql)) {
                        JOptionPane.showMessageDialog(
                                this, "sql不能为空", "失败", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    JPanel jPanelnew = getResultPanel(runSql, null);
                    this.getParent().repaint();
                });
        tree.addTreeSelectionListener(
                x -> {
                    String tableName = x.getNewLeadSelectionPath().getPath()[1].toString();
                    String sql = MessageFormat.format("select * from {0} ", tableName);
                    jTextArea.setText(sql);
                    btnQuery.doClick();
                });
    }

    private JPanel getResultPanel(String querySql, LinkedHashMap<String, String> cloumnMapping) {
        PagingTable pagingTable = new PagingTable(querySql, cloumnMapping);
        JPanel panel = pagingTable.getPagingTable();
        return panel;
    }

    private JPanel getDbTree() {
        JPanel panel = new JPanel();
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("表信息");
        String querySql = "SELECT name FROM sqlite_master where type='table' order by name";

        ISqlMapper iSqlMapper = MybatisUtil.getSqlSession().getMapper(ISqlMapper.class);
        List<Map<String, Object>> list = iSqlMapper.selectList(querySql);
        list.stream()
                .forEach(
                        x -> {
                            String tableName = x.get("name").toString();
                            DefaultMutableTreeNode node = new DefaultMutableTreeNode(tableName);
                            root.add(node);
                        });
        tree = new JTree(root);
        panel.add(tree);
        panel.setVisible(true);
        return panel;
    }
}
