package net.gzcx.ui.pagingTable;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.gzcx.mapper.ISqlMapper;
import net.gzcx.utils.MybatisUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/** 分页的JTable */
@Getter
@Slf4j
public class PagingTable extends JPanel {
    private String querySql;
    private LinkedHashMap<String, String> coloumnName;
    private JTable table = new JTable();
    private JScrollPane jScrollPane = null;

    @Override
    public boolean getInheritsPopupMenu() {
        return super.getInheritsPopupMenu();
    }

    public PagingTable(String querySql, LinkedHashMap<String, String> coloumnName) {
        this.querySql = querySql;
        this.coloumnName = coloumnName;
    }

    public JPanel getPagingTable() {
        StatusBar statusBar = new StatusBar();
        statusBar.loadData();
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        jScrollPane = new JScrollPane(table);
        JPanel jPanel = new JPanel(new BorderLayout());
        jPanel.add(jScrollPane, BorderLayout.CENTER);
        jPanel.add(statusBar, BorderLayout.SOUTH);
        return jPanel;
    }

    class StatusBar extends AbstractStatusBar {
        @Override
        public void fillToTable(RgPageInfo rgPageInfo) {
            // TODO Auto-generated method stub
            // 得到当前页查询语句
            PageHelper.startPage(rgPageInfo.getCurPage(), rgPageInfo.getPageSize());
            // 利用当前页查询语句cursql做你自己的事情，如：查询出的数据填充到table
            ISqlMapper iSqlMapper = MybatisUtil.getSqlSession().getMapper(ISqlMapper.class);
            List<Map<String, Object>> list = iSqlMapper.selectList(PagingTable.this.getQuerySql());
            PageInfo<Map<String, Object>> pageInfo = new PageInfo<Map<String, Object>>(list);
            int row = (int) pageInfo.getTotal();
            if(null!=list && list.size()>0){
                int cols = list.get(0).size();

                int totalCount = pageInfo.getNavigatePages();
                rgPageInfo.setTotalSize(totalCount);
                rgPageInfo.setTotalPage((int) Math.ceil(totalCount / rgPageInfo.getPageSize()));

                Object[][] playerInfo = new Object[row][cols];
                int i = 0, j = 0;
                for (Map<String, Object> map : pageInfo.getList()) {
                    Set<String> setKey = map.keySet();
                    if (null != PagingTable.this.getColoumnName()) {
                        setKey = PagingTable.this.getColoumnName().keySet();
                    }
                    for (String key : setKey) {
                        playerInfo[i][j] = map.get(key);
                        j++;
                    }
                    i++;
                    j = 0;
                }

                Object[] cloumNameArrs = null;
                if (null != PagingTable.this.getColoumnName()) {
                    cloumNameArrs = PagingTable.this.getColoumnName().values().toArray();
                }

                /** 刷新数据 */
                DefaultTableModel tabModel = new DefaultTableModel(playerInfo, cloumNameArrs);
                table.setModel(tabModel);
            }
        }
    }
}
