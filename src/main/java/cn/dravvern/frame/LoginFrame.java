package cn.dravvern.frame;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import cn.dravvern.base.UserInfo;

public class LoginFrame extends JFrame {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    JTextField usernamefield;
    JPasswordField passwordfield;

    public LoginFrame() {
        setTitle("用户登录");
        setSize(280, 160);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setLayout(new FlowLayout());
        setResizable(false);// 设置窗口不可以改变大小
        // setAlwaysOnTop(true);// 设置窗口总在最前方

        usernamefield = new JTextField(10);
        passwordfield = new JPasswordField(20);
        JButton loginbutton = new JButton("登录");
        JButton regbutton = new JButton("注册");
        regbutton.setVisible(false);

        Box box1 = Box.createVerticalBox();
        box1.add(new JLabel("账号："));
        box1.add(Box.createVerticalStrut(20));
        box1.add(new JLabel("密码："));

        Box box2 = Box.createVerticalBox();
        box2.add(usernamefield);
        box2.add(Box.createVerticalStrut(20));
        box2.add(passwordfield);

        Box box3 = Box.createHorizontalBox();
        box3.add(loginbutton);
        box3.add(Box.createHorizontalStrut(8));
        box3.add(regbutton);

        Box baseB1 = Box.createHorizontalBox();
        baseB1.add(box1);
        baseB1.add(Box.createHorizontalStrut(8));
        baseB1.add(box2);

        Box baseB2 = Box.createVerticalBox();
        baseB2.add(baseB1);
        baseB2.add(Box.createVerticalStrut(20));
        baseB2.add(box3);

        add(baseB2);

        loginbutton.addActionListener(new LoginButtonActionListener());
        regbutton.addActionListener(new RegButtonActionListener());
    }

    class LoginButtonActionListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            String username = usernamefield.getText().toString();
            char[] passwords = passwordfield.getPassword();
            String pwd = turnCharsToString(passwords);
            String errmsg = UserInfo.getInstance().checkLogin(username, pwd);

            if (errmsg != null) {
                JOptionPane.showMessageDialog(getContentPane(), errmsg, "友情提示", JOptionPane.INFORMATION_MESSAGE);
            } else {
                MainFrame mainframe = new MainFrame();
                setVisible(false);
                
                Toolkit toolkit = Toolkit.getDefaultToolkit();
                Dimension screenSize = toolkit.getScreenSize();
                Dimension frameSize = mainframe.getSize();
                if (frameSize.width > screenSize.width) {
                    frameSize.width = screenSize.width;
                }
                if (frameSize.height > screenSize.height) {
                    frameSize.height = screenSize.height;
                }
                mainframe.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
                mainframe.setVisible(true);
            }
        }
    }

    class RegButtonActionListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {

        }
    }

    private String turnCharsToString(char[] chars) {
        StringBuffer strBuf = new StringBuffer();
        for (int i = 0; i < chars.length; i++) {
            strBuf.append(chars[i]);
        }
        return strBuf.toString().trim();
    }
}
