//package tech.chenxing;
//
//import org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper;
//import org.jb2011.lnf.beautyeye.utils.Platform;
//import tech.chenxing.popup.CoolPopupFactory;
//import tech.chenxing.ui.page.AbstractStatusBar;
//import tech.chenxing.ui.page.BaseDAO;
//import tech.chenxing.ui.pagingTable.RgPageInfo;
//
//import javax.swing.*;
//import javax.swing.table.DefaultTableModel;
//import java.awt.*;
//import java.util.List;
//
//public class Test {
//    JTable table ;
//    StatusBar statusBar ;
//    JFrame jf = new JFrame("人工工具箱之分页表格");
//    JScrollPane jScrollPaneJtable=null;
//    BaseDAO baseDAO=new BaseDAO();
//    Test(){
//        table =new JTable();
//        statusBar=new StatusBar();
//        jf.setSize(800,500);
//        jf.setLayout(new BorderLayout());
//        jScrollPaneJtable=new JScrollPane(table);
//        jf.add(jScrollPaneJtable,BorderLayout.CENTER);
//        jf.add(statusBar,BorderLayout.SOUTH);
//        jf.pack();
//        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        jf.setVisible(true);
//    }
//    //查询方法
//    public void loaddata(){
//
//        statusBar.loadData();
//    }
//
//    class StatusBar extends AbstractStatusBar {
//
//        @Override
//        public String getSql() {
//            // TODO Auto-generated method stub
//            // 组装全部查询语句
//            String sql = "SELECT  flow_uuid,business_scenario  FROM flow_base_info WHERE id<1000";
//            return sql;
//        }
//
//        @Override
//        public void fillToTable(RgPageInfo rgPageInfo) {
//            // TODO Auto-generated method stub
//            //得到当前页查询语句
//            String cursql = rgPageInfo.getCurQuerySQL();
//            //利用当前页查询语句cursql做你自己的事情，如：查询出的数据填充到table
//            List<Object[]>  list=baseDAO.excuteQuery(cursql);
//            int row=list.size();
//            int cols=list.get(0).length;
//
//            Object[][] playerInfo = new Object[row][cols];
//            int i = 0, j = 0;
//            for (Object[] objects : list) {
//                for (Object kObject : objects) {
//                    playerInfo[i][j] = kObject;
//                    j++;
//                }
//                i++;
//                j = 0;
//            }
//            /**
//             * 刷新数据
//             */
//            DefaultTableModel tabModel = new DefaultTableModel(playerInfo, new Object[]{"flow_uuid","business_scenario"});
//             table.setModel(tabModel);
//             table.setEnabled(true);
//
//
//        }
//    }
//
//    public static void main(String[] args) {
//
//
//        initUserInterface();
//        SwingUtilities.invokeLater(
//                new Runnable() {
//                    @Override
//                    public void run() {
//                        Test test=new Test();
//                        test.loaddata();
//                    }
//                });
//    }
//
//
//    private static void initUserInterface() {
//        System.setProperty("apple.laf.useScreenMenuBar", "true");
//        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Draw 9-patch");
//
//        try {
//            if (Platform.isWindows()) {
//                UIManager.put("RootPane.setupButtonVisible", false);
//                BeautyEyeLNFHelper.translucencyAtFrameInactive = false;
//                BeautyEyeLNFHelper.launchBeautyEyeLNF();
//
//                // impl a demo PopupFactory
//                PopupFactory.setSharedInstance(new CoolPopupFactory());
//            } else {
//                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//            }
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        } catch (InstantiationException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (UnsupportedLookAndFeelException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//}
