package net.gzcx;

import net.gzcx.popup.CoolPopupFactory;
import org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper;
import org.jb2011.lnf.beautyeye.utils.Platform;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

@SuppressWarnings("serial")
public class StudentTableTest extends JFrame implements ActionListener {
    private JScrollPane panel;
    private JButton next, previous, add, delete;
    private JLabel label1;
    private StudentTable table;

    public StudentTableTest() {
        super("表分页及操作");
        initTableData();
        initComponent();
    }

    private void initTableData() {
        // TODO Auto-generated method stub
        Student s = new Student(1, "yangfei", "男", 21);
        Student.students.add(s);
        s = new Student(2, "yangf", "女", 22);
        Student.students.add(s);
        s = new Student(3, "yangfei", "男", 23);
        Student.students.add(s);
        s = new Student(4, "yangf", "女", 24);
        Student.students.add(s);
        s = new Student(5, "yangfei", "男", 25);
        Student.students.add(s);
        s = new Student(6, "yangf", "女", 26);
        Student.students.add(s);
        s = new Student(7, "yangfei", "男", 27);
        Student.students.add(s);
        s = new Student(8, "yangf", "女", 28);
        Student.students.add(s);
    }

    private void initComponent() {
        // TODO Auto-generated method stub
        this.setSize(800, 500);
        table = new StudentTable();
        panel = new JScrollPane(table);
        panel.setBounds(10, 10, 670, 119);
        previous = new JButton("上一页");
        previous.setBounds(150, 150, 75, 20);
        next = new JButton("下一页");
        next.setBounds(255, 150, 75, 20);
        add = new JButton("添加");
        add.setBounds(350, 150, 65, 20);
        delete = new JButton("删除");
        delete.setBounds(420, 150, 65, 20);
        previous.addActionListener(this);
        next.addActionListener(this);
        add.addActionListener(this);
        delete.addActionListener(this);
        label1 = new JLabel("总共" + table.totalRowCount + "记录|当前第"
                + table.currentPage + "页");
        label1.setBounds(10, 150, 130, 20);
        this.getContentPane().setLayout(null);
        this.getContentPane().add(panel);
        this.getContentPane().add(previous);
        this.getContentPane().add(next);
        this.getContentPane().add(add);
        this.getContentPane().add(delete);
        this.getContentPane().add(label1);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    /**
     * 按钮事件
     */
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub
        JButton button = (JButton) e.getSource();
        if (button.equals(previous)) {
            int i = table.getPreviousPage();
            if (i == -1)
                return;
        }
        if (button.equals(next)) {
            int i = table.getNextPage();
            if (i == -1)
                return;
        }
        if (button.equals(delete)) {
            int i = table.getSelectedRow();
            if (i == -1)
                return;
            Integer id = (Integer) table.getValueAt(i, 0);
            if (id == null)
                return;
            Student s = null;
            for (Student stu : Student.students) {
                if (stu.getId().equals(id))
                    s = stu;
            }
            int index = Student.students.indexOf(s);
            Student.students.remove(index);
            table.initTable();
            label1.setText("总共" + table.totalRowCount + "记录|当前第"
                    + table.currentPage + "页");
            return;
        }
        if (button.equals(add)) {
            Integer id = 0;
            for (Student stu : Student.students) {
                if (stu.getId() > id)
                    id = stu.getId();
            }
            Student student = new Student(id + 1, "wuming" + (id + 1), "男", 22);
            Student.students.add(student);
            table.initTable();
            label1.setText("总共" + table.totalRowCount + "记录|当前第"
                    + table.currentPage + "页");
            return;
        }
        DefaultTableModel model = new DefaultTableModel(table.getPageData(),
                table.columnNames);
        table.setModel(model);
        label1.setText("总共" + table.totalRowCount + "记录|当前第"
                + table.currentPage + "页");
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
      //  initUserInterface();
        SwingUtilities.invokeLater(
                new Runnable() {
                    @Override
                    public void run() {
                        JFrame  frame=new StudentTableTest();
                        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
                        frame.setLocationRelativeTo(null);
                        frame.setVisible(true);
                        //
                        //				FixtipPane fp = new FixtipPane();
                        //				fp.show(frame, frame.getJMenuBar(),0,10);
                    }
                });
    }
        // TODO Auto-generated method stub

    private static void initUserInterface() {
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Draw 9-patch");

        try {
            if (Platform.isWindows()) {
                UIManager.put("RootPane.setupButtonVisible", false);
                BeautyEyeLNFHelper.translucencyAtFrameInactive = false;
                BeautyEyeLNFHelper.launchBeautyEyeLNF();

                // impl a demo PopupFactory
                PopupFactory.setSharedInstance(new CoolPopupFactory());
            } else {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}