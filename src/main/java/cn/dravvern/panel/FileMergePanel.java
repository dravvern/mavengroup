package cn.dravvern.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import cn.dravvern.thread.FileMergeThread;

/**   
 *    
 * 项目名称：mavengroup   
 * 类名称：FileMergePanel   
 * 类描述：   
 * 创建人：dravvern   
 * 创建时间：2017年10月9日 下午12:41:21   
 * 修改人：dravvern   
 * 修改时间：2017年10月9日 下午12:41:21   
 * 修改备注：   
 * @version    
 *    
 */
public class FileMergePanel extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private JTextField sourceTextField;
    private File zipFile;
    private JTextField targetTextField;
    private File targetFile;
    
    public FileMergePanel(final JPanel contentPane) {
        setBounds(200, 200, 600, 400);
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        
        JPanel choosePanel = new JPanel();
        choosePanel.setBorder(BorderFactory.createLineBorder(Color.green));
        contentPane.add(choosePanel, BorderLayout.NORTH);
        
        sourceTextField = new JTextField();
        
        sourceTextField.setColumns(15);
        
        JButton sourceButton = new JButton("待合并文件");
        sourceButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doSourceButtonActionPerformed(e);
            }
        });
        
        targetTextField = new JTextField();
        targetTextField.setColumns(15);
        
        JButton targetButton = new JButton("输出文件");
        targetButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doTargetButtonActionPerformed(e);
            }
        });
        
        choosePanel.add(sourceTextField);
        choosePanel.add(sourceButton);
        choosePanel.add(targetTextField);
        choosePanel.add(targetButton);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createLineBorder(Color.blue));
        contentPane.add(buttonPanel, BorderLayout.SOUTH);
        
        JButton unzipButton = new JButton("开始转换");
        unzipButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doUnzipButtonActionPerformed(e);
            }
        });
        buttonPanel.add(unzipButton);
        
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.red));
        contentPane.add(scrollPane, BorderLayout.CENTER);
        
        JTextArea instructions  = new JTextArea("使用说明:\n");
        instructions.setLineWrap(true);
        scrollPane.setViewportView(instructions);
        
        instructions.append("把多个csv/xls/xlsx文件 压缩在一个zip文件中, 程序会把这些文件合并到一个xlsx文件中 \n");
        instructions.append("文件头会取第一个文件的文件头。 \n");
        instructions.append("选择好输入文件(zip)和输出文件(xlsx)后，按开始转换进行转换\n");
        instructions.append("看下方日志出现“文件合并完毕”后合并结束。\n");
    }
    
    protected void doSourceButtonActionPerformed(ActionEvent e) {
        JFileChooser fcDlg = new JFileChooser();
        fcDlg.setDialogTitle("请选择待合并的Execl文件的压缩包...");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("压缩文件(*.zip)", "zip");
        fcDlg.setFileFilter(filter);
        int returnVal = fcDlg.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            zipFile = fcDlg.getSelectedFile();
            sourceTextField.setText(zipFile.getAbsolutePath());
        }
    }
    
    protected void doTargetButtonActionPerformed(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Execl文件(*.xlsx)", "xlsx");
        chooser.setFileFilter(filter);

        int option = chooser.showSaveDialog(null);
        if (option == JFileChooser.APPROVE_OPTION) {// 假如用户选择了保存
            File file = chooser.getSelectedFile();
            String fname = chooser.getName(file); // 从文件名输入框中获取文件名
            if (fname.indexOf(".xlsx") == -1) {
                file = new File(chooser.getCurrentDirectory(), fname + ".xlsx");
            }
            targetFile = file;
            targetTextField.setText(targetFile.getAbsolutePath());
        }
    }
    
    protected void doUnzipButtonActionPerformed(ActionEvent e) {
        String sourceFilename = sourceTextField.getText().toString();
        String targetFilename = targetTextField.getText().toString();
        File zfile = new File(sourceFilename);
        
        if (targetFilename == null || targetFilename.length() == 0) {
            JOptionPane.showMessageDialog(null, "请选择输出文件", "ERROR", JOptionPane.INFORMATION_MESSAGE);
        } else if (!(zfile.exists() && zfile.isFile())) {
            JOptionPane.showMessageDialog(null, "待合并文件不存在", "ERROR", JOptionPane.INFORMATION_MESSAGE);
        } else {
            File tFile = new File(targetFilename);
            FileMergeThread mfThread = new FileMergeThread(zfile, tFile);
            mfThread.start();
        }
    }
}
