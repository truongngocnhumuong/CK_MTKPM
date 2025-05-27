package librarygui;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import observer.Observer;
import singleton.*;

public class MainLibraryGUI extends JFrame implements Observer {
    private BookManager bookManager;
    private MemberManager memberManager;
    private LoanManager loanManager;
    private BookPanel bookPanel;
    private MemberPanel memberPanel;
    private LoanPanel loanPanel;
    private ReportPanel reportPanel;

    public MainLibraryGUI() {
        // Initialize managers
        bookManager = BookManager.getInstanse();
        memberManager = MemberManager.getInstance();
        loanManager = LoanManager.getInstance();

        setTitle("Hệ Thống Quản Lý Thư Viện");
        setSize(1024, 768);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main content panel with 10px padding
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(0, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Title Panel (5% of height)
        JPanel titlePanel = new JPanel();
        titlePanel.setPreferredSize(new Dimension(1024, 38));
        titlePanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        JLabel titleLabel = new JLabel("QUẢN LÝ THƯ VIỆN");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titlePanel.add(titleLabel);
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        // Initialize panels
        bookPanel = new BookPanel(this);
        memberPanel = new MemberPanel(this);
        loanPanel = new LoanPanel(this);
        reportPanel = new ReportPanel(this);

        // Create tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Sách", bookPanel);
        tabbedPane.addTab("Thành Viên", memberPanel);
        tabbedPane.addTab("Mượn Trả", loanPanel);
        tabbedPane.addTab("Báo Cáo", reportPanel);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        add(mainPanel);
    }

    @Override
    public void update(String event, Object data) {
        SwingUtilities.invokeLater(() -> {
            bookPanel.updateData();
            memberPanel.updateData();
            loanPanel.updateData();
            reportPanel.updateData();
        });
    }

    public BookManager getBookManager() {
        return bookManager;
    }

    public MemberManager getMemberManager() {
        return memberManager;
    }

    public LoanManager getLoanManager() {
        return loanManager;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainLibraryGUI().setVisible(true));
    }
} 