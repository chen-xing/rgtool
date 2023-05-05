package net.gzcx.tool;

import net.gzcx.utils.CalculatorUtil;
import net.gzcx.utils.ConsoleUtil;
import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Locale;

/**
 * @author chen xing
 * @description 计算器
 * @date 2023-05-05 15:04
 */
public class Calculator extends JPanel {
    private JLabel tipsLable = new JLabel("四则运算");
    private JTextField expressionTextField = new JTextField();

    private JButton confirmBtn = new JButton("=");

    private JTextArea resultTextArea = new JTextArea();

    public Calculator() {
        super(new BorderLayout());
        initGUI();
        initListeners();
    }

    private void initGUI() {
        // init sub coms
        resultTextArea.setLineWrap(true);

        confirmBtn.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.red));
        confirmBtn.setForeground(Color.white);
        confirmBtn.setPreferredSize(new Dimension(60, 40));

        Font font1 = getTestFont(18);
        Font font2 = getTestFont(24);

        tipsLable.setFont(font1);
        confirmBtn.setFont(font2);

        expressionTextField.setEditable(true);
        expressionTextField.setPreferredSize(new Dimension(600, 40));
        expressionTextField.setFont(font2);
        resultTextArea.setFont(font1);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        // btnPanel.setBorder(BorderFactory.createTitledBorder("输入区"));

        btnPanel.add(tipsLable);
        btnPanel.add(expressionTextField);
        btnPanel.add(confirmBtn);

        this.add(btnPanel, BorderLayout.NORTH);
        this.add(new JScrollPane(resultTextArea), BorderLayout.CENTER);
        this.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
    }

    private void initListeners() {
        confirmBtn.addActionListener(
                e -> {
                    calculatorExpression();
                });
        expressionTextField.addKeyListener(
                new KeyListener() {
                    @Override
                    public void keyTyped(KeyEvent e) {}

                    @Override
                    public void keyPressed(KeyEvent e) {
                        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                            calculatorExpression();
                        }
                    }

                    @Override
                    public void keyReleased(KeyEvent e) {}
                });
    }

    /** 执行计算 */
    private void calculatorExpression() {
        String result = null;
        String inputExpress = expressionTextField.getText().replace("（", "(").replace("）", ")");
        inputExpress = inputExpress.replace(",", "");
        inputExpress = inputExpress.replace("\t", "");
        net.gzcx.utils.Calculator calc = new net.gzcx.utils.Calculator();
        Double str = null;
        try {
            str = calc.prepareParam(inputExpress + "=");
            String resultStr =
                    CalculatorUtil.formatResult(
                            String.format(
                                    "%." + CalculatorUtil.RESULT_DECIMAL_MAX_LENGTH + "f", str));
            result = resultStr;
        } catch (Exception e) {
            result = e.getMessage();
        }
        result = inputExpress + "=" + result;
        ConsoleUtil.consoleOnly(resultTextArea, result);
    }

    /**
     * 设置字体
     *
     * @return
     */
    private Font getTestFont(int size) {
        Font font = getCustomize(null, -1, size, tipsLable.getFont());
        return font;
    }

    private Font getCustomize(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) {
            return null;
        }
        ;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        Font font =
                new Font(
                        resultName,
                        style >= 0 ? style : currentFont.getStyle(),
                        size >= 0 ? size : currentFont.getSize());
        boolean isMac =
                System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
        Font fontWithFallback =
                isMac
                        ? new Font(font.getFamily(), font.getStyle(), font.getSize())
                        : new StyleContext()
                                .getFont(font.getFamily(), font.getStyle(), font.getSize());
        return fontWithFallback instanceof FontUIResource
                ? fontWithFallback
                : new FontUIResource(fontWithFallback);
    }
}
