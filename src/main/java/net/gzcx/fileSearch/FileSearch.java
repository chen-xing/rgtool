package net.gzcx.fileSearch;

import cn.hutool.core.io.FileUtil;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import com.google.common.collect.Lists;
import com.timevale.mandarin.base.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import net.gzcx.utils.SystemConfig;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.util.Version;
import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;

/**
 * @author chen xing
 * @description 本地文件
 * @date 2021-10-29 10:35
 */
@Slf4j
public class FileSearch extends JPanel {
    private JButton indexButton = new JButton("创建索引");
    private JButton searchButton = new JButton("检索");
    private JTextField keywordTextField = new JTextField();
    // private JScrollPane jScrollPane = null;
    private JTable resultTable = new JTable();

    public FileSearch() {
        super(new BorderLayout());
        initGUI();
        initListeners();
        // initCreateIndexTask();
    }

    /**
     * @author chen xing
     * @description 初始话页面
     * @param
     * @return void
     * @date 2021-10-29 11:10
     */
    private void initGUI() {
        indexButton.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.blue));
        indexButton.setForeground(Color.white);

        searchButton.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.red));
        searchButton.setForeground(Color.white);

        keywordTextField.setEditable(true);
        keywordTextField.setPreferredSize(new Dimension(800, 30));

        resultTable.setRowHeight(30); // 行高
        resultTable.setFont(new Font("黑体", Font.PLAIN, 12)); // 字体、颜色、大小
        resultTable.setShowHorizontalLines(false); // 显示行的分割线
        resultTable.setShowVerticalLines(false); // 显示列的分割线
        resultTable.setIntercellSpacing(new Dimension(0, 1)); // 单元格的间隔
        resultTable.setGridColor(new Color(220, 220, 220)); // 分割线的颜色
        resultTable.setColumnSelectionAllowed(false); // 是否允许选中一整列
        resultTable.setRowSelectionAllowed(true); // 是否允许选中一整行

        Component table;
        // jScrollPane = new JScrollPane(resultTable);

        // init btn pane
        JPanel btnPane = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnPane.add(indexButton);
        btnPane.add(keywordTextField);
        btnPane.add(searchButton);

        // init main ui
        this.add(btnPane, BorderLayout.NORTH);
        this.add(resultTable, BorderLayout.CENTER);
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }

    /**
     * @author chen xing
     * @description 初始化监听器
     * @param
     * @return void
     * @date 2021-10-29 11:10
     */
    private void initListeners() {
        searchButton.addActionListener(
                x -> {
                    searchResult();
                });

        searchButton.registerKeyboardAction(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        searchResult();
                    }
                },
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

        indexButton.addActionListener(
                x -> {
                    try {
                        creatIndex();
                    } catch (IOException | ParseException e) {
                        JOptionPane.showMessageDialog(this, "创建索引失败！");
                        log.error("create index error", e);
                    }
                });
    }

    /**
     * @author chen xing
     * @description 在指定的索引中检索关键字
     * @param keyWords
     * @return java.util.List<tech.chenxing.fileSearch.Articles>
     * @date 2021-10-29 11:44
     */
    public java.util.List<Articles> serchWithKeyWord(String keyWords) throws Exception {
        java.util.List<Articles> articlesList = Lists.newArrayList();

        // 创建IndexSearcher对象
        IndexSearcher indexSearcher = new IndexSearcher(LuceneUtils.getDirectory());
        // 创建QueryParser对象
        QueryParser queryParser =
                new QueryParser(Version.LUCENE_30, "context", LuceneUtils.getAnalyzer());
        // 创建Query对象来封装关键字
        Query query = queryParser.parse(keyWords);
        // 用IndexSearcher对象去索引库中查询符合条件的前100条记录，不足100条记录的以实际为准
        TopDocs topDocs = indexSearcher.search(query, 100);

        Highlighter highlighter =
                new Highlighter(
                        new SimpleHTMLFormatter(
                                "<font color='red'><b>", "</b></font>"), // 高亮格式，用<B>标签包裹
                        new QueryScorer(query));
        Fragmenter fragmenter = new SimpleFragmenter(100); // 高亮后的段落范围在100字内
        highlighter.setTextFragmenter(fragmenter);

        // 获取符合条件的编号
        for (int i = 0; i < topDocs.scoreDocs.length; i++) {
            ScoreDoc scoreDoc = topDocs.scoreDocs[i];
            int no = scoreDoc.doc;
            // 用indexSearcher对象去索引库中查询编号对应的Document对象
            Document document = indexSearcher.doc(no);
            // 将Document对象中的所有属性取出，再封装回JavaBean对象中去
            Articles articles = (Articles) LuceneUtils.Document2JavaBean(document, Articles.class);

            String hightlignContent =
                    highlighter.getBestFragment(
                            LuceneUtils.getAnalyzer(), "context", document.get("context"));
            hightlignContent = MessageFormat.format("<html>{0}</html>", hightlignContent);
            articles.setContext(hightlignContent);

            articlesList.add(articles);
        }
        return articlesList;
    }

    /**
     * @author chen xing
     * @description list转二维数组
     * @param articlesList
     * @return java.lang.Object[][]
     * @date 2021-10-29 14:32
     */
    private Object[][] convertToArrry(java.util.List<Articles> articlesList) {
        Object[][] result = new Object[articlesList.size()][2];
        int j = 0;
        for (Articles articles : articlesList) {
            Object[] object = new Object[2];
            object[0] = articles.getPath();
            object[1] = articles.getContext();
            result[j++] = object;
        }
        return result;
    }

    /**
     * @author chen xing
     * @description 启动定时索引的任务
     * @param
     * @return void
     * @date 2021-10-29 16:56
     */
    private void initCreateIndexTask() {
        CronUtil.schedule(
                SystemConfig.getCreateIndexCron(),
                new Task() {
                    @Override
                    public void execute() {
                        try {
                            log.info("start execute createIndex");
                            creatIndex();
                            log.info("end execute createIndex");
                        } catch (IOException | ParseException e) {
                            log.error("create index error", e);
                        }
                    }
                });
        try {
            CronUtil.start();
        } catch (Exception ex) {
            log.error("start task error", ex);
        }
    }

    /**
     * @author chen xing
     * @description 创建索引
     * @param
     * @return void
     * @date 2021-10-29 16:59
     */
    private void creatIndex() throws IOException, ParseException {
        IndexWriter indexWriter =
                new IndexWriter(
                        LuceneUtils.getDirectory(),
                        LuceneUtils.getAnalyzer(),
                        LuceneUtils.getMaxFieldLength());
        Document doc = null; // 我们索引的有可能是一段文本or数据库中的一张表
        Articles articles = null;

        String searchDir = SystemConfig.getLuceneSearchDir();
        String[] dirArr = searchDir.split(";");

        for (String dir : dirArr) {
            for (File file : FileUtil.loopFiles(dir)) {
                if (file.isDirectory()) {
                    continue;
                }

                String path = file.getAbsolutePath();
                String context = CommonUtil.getContentFromFile(file);
                String hash = CommonUtil.getHash(context);

                FileStatusEnum fileStatusEnum = checkFileStatus(path, hash);
                if (FileStatusEnum.EXISTS_NOT_UPDATE.equals(fileStatusEnum)) {
                    continue;
                }
                if (FileStatusEnum.EXISTS_UPDATE.equals(fileStatusEnum)) {
                    deleteRecord(indexWriter, path);
                }

                articles = new Articles();
                articles.setBizId(CommonUtil.getHash(path));
                articles.setAuthor("人工博客");

                articles.setContext(context);
                articles.setHash(hash);
                articles.setPath(path);
                doc = LuceneUtils.javaBean2Document(articles);

                // 将Document对象通过IndexWriter对象写入索引库中
                indexWriter.addDocument(doc);
            }
        }
        indexWriter.close();
    }

    /**
     * @author chen xing
     * @description 判断当前的内容是否是需要更新
     * @param path
     * @param md5
     * @return boolean
     * @date 2021-10-29 17:28
     */
    private FileStatusEnum checkFileStatus(String path, String md5)
            throws IOException, ParseException {
        // 创建IndexSearcher对象
        IndexSearcher indexSearcher = new IndexSearcher(LuceneUtils.getDirectory());
        // 创建QueryParser对象
        QueryParser queryParser =
                new QueryParser(Version.LUCENE_30, "bizId", LuceneUtils.getAnalyzer());
        // 创建Query对象来封装关键字
        Query query = queryParser.parse(CommonUtil.getHash(path));
        // 用IndexSearcher对象去索引库中查询符合条件的前100条记录，不足100条记录的以实际为准
        TopDocs topDocs = indexSearcher.search(query, 1);
        if (null == topDocs || topDocs.scoreDocs.length == 0) {
            return FileStatusEnum.NOT_EXISTS;
        }
        ScoreDoc scoreDoc = topDocs.scoreDocs[0];
        int no = scoreDoc.doc;
        // 用indexSearcher对象去索引库中查询编号对应的Document对象
        Document document = indexSearcher.doc(no);
        // 将Document对象中的所有属性取出，再封装回JavaBean对象中去
        Articles articles = (Articles) LuceneUtils.Document2JavaBean(document, Articles.class);
        if (articles.getHash().equals(md5)) {
            return FileStatusEnum.EXISTS_NOT_UPDATE;
        } else {
            return FileStatusEnum.EXISTS_UPDATE;
        }
    }

    /**
     * @author chen xing
     * @description 删除指定的索引记录
     * @param path
     * @return void
     * @date 2021-10-29 17:29
     */
    private void deleteRecord(IndexWriter indexWriter, String path) throws IOException {
        indexWriter.deleteDocuments(new Term("bizId", CommonUtil.getHash(path)));
        indexWriter.commit(); // 提交删除操作
    }

    /**
     * @author chen xing
     * @description 根据关键字进行检索
     * @param
     * @return void
     * @date 2021-11-01 16:37
     */
    private void searchResult() {
        String keyWords = this.keywordTextField.getText().trim();
        if (StringUtils.isBlank(keyWords)) {
            JOptionPane.showMessageDialog(this, "检索的关键字不能为空！");
        } else {
            try {
                java.util.List<Articles> articlesList = serchWithKeyWord(keyWords);
                Object[][] data = convertToArrry(articlesList);
                Object[] header = new Object[] {"路径", "内容"};
                DefaultTableModel tabModel = new DefaultTableModel(data, header);
                resultTable.setModel(tabModel);

            } catch (Exception e) {
                log.error("检索关键字为：{}", keyWords, e);
            }
        }
    }
}
