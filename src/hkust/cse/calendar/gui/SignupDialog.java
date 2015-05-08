package hkust.cse.calendar.gui;

import hkust.cse.calendar.system.UserFactory;
import hkust.cse.calendar.unit.Admin;
import hkust.cse.calendar.unit.RegularUser;
import hkust.cse.calendar.unit.TimeSpan;
import hkust.cse.calendar.unit.User;
import hkust.cse.calendar.userstorage.UserStorageController;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Timestamp;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;


public class SignupDialog extends JDialog implements ActionListener {
	private UserStorageController userController;
	private JTextField usernameField;
	private JPasswordField passwordField;
	private JPasswordField rePasswordField;
	private JTextField firstNameField;
	private JTextField lastNameField;
	private JTextField emailField;

	private JButton signUpButton;
	private JButton cancelButton;
	private JCheckBox adminCheckBox;
	private User newUser;

	public SignupDialog() {
		userController = UserStorageController.getInstance();
		newUser = null;
		
		setTitle("Sign up");
		setAlwaysOnTop(true);

		Container contentPane;
		contentPane = getContentPane();

		JPanel top = new JPanel();
		Border loginInfoBorder = new TitledBorder("Login Information");
		top.setBorder(loginInfoBorder);
		top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));

		JPanel usernamePanel = new JPanel();
		usernamePanel.add(new JLabel("Username: "));
		usernameField = new JTextField(10);
		usernamePanel.add(usernameField);
		top.add(usernamePanel);

		JPanel passwordPanel = new JPanel();
		passwordPanel.add(new JLabel("Password: "));
		passwordField = new JPasswordField(10);
		passwordPanel.add(passwordField);
		top.add(passwordPanel);

		JPanel rePasswordPanel = new JPanel();
		rePasswordPanel.add(new JLabel("Re-type Password: "));
		rePasswordField = new JPasswordField(10);
		rePasswordPanel.add(rePasswordField);
		top.add(rePasswordPanel);
		
		JPanel adminPanel = new JPanel();
		adminCheckBox = new JCheckBox("Sign up as admin");
		adminCheckBox.addActionListener(this);
		adminPanel.add(adminCheckBox);
		top.add(adminPanel);

		contentPane.add("North", top);

		JPanel personalInfoPanel = new JPanel();
		Border personalInfoBorder = new TitledBorder("Personal Information");
		personalInfoPanel.setBorder(personalInfoBorder);
		personalInfoPanel.setLayout(new BoxLayout(personalInfoPanel, BoxLayout.Y_AXIS));

		JPanel namePanel = new JPanel();
		namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.X_AXIS));
		namePanel.add(new JLabel("First Name: "));
		firstNameField = new JTextField(10);
		namePanel.add(firstNameField);
		namePanel.add(new JLabel("Last Name: "));
		lastNameField = new JTextField(10);
		namePanel.add(lastNameField);
		personalInfoPanel.add(namePanel);

		JPanel emailPanel = new JPanel();
		emailPanel.add(new JLabel("Email: "));
		emailField = new JTextField(30);
		emailPanel.add(emailField);
		personalInfoPanel.add(emailPanel);

		contentPane.add("Center", personalInfoPanel);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

		signUpButton = new JButton("Sign up now");
		signUpButton.addActionListener(this);
		buttonPanel.add(signUpButton);

		cancelButton = new JButton("Canel");
		cancelButton.addActionListener(this);
		buttonPanel.add(cancelButton);

		contentPane.add("South", buttonPanel);

		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == cancelButton) {
			setVisible(false);
			dispose();
		}
		else if(e.getSource() == signUpButton){
			boolean succeed = sigUpButtonResponse();
			if(succeed) {
				JOptionPane.showMessageDialog(this, "Successful Register!");
				setVisible(false);
				dispose();
			}
		}
	}
	
	private boolean sigUpButtonResponse() {
		String userId = checkValidUserId(usernameField.getText());
		if(userId == null) {
			return false;
		}

		String pw = checkValidPassword(passwordField.getText(), rePasswordField.getText());
		if(pw == null) {
			return false;
		}

		String firstName = checkValidFirstName(firstNameField.getText());
		String lastName = checkValidLastName(lastNameField.getText());
		
		if(firstName == null || lastName == null) {
			return false;
		}

		String email = checkValidEmail(emailField.getText());
		if(email == null) {
			return false;
		}
		
		if(adminCheckBox.isSelected()) {
				newUser = new Admin(userId, pw);
				newUser = UserFactory.getInstance().createAccount("Admin", userId, pw);
		}
		else {
			newUser = UserFactory.getInstance().createAccount("Regular", userId, pw);
		}
		
		newUser.setName(firstName, lastName);
		newUser.setEmail(email);
		
		userController.manageUsers(newUser, UserStorageController.NEW);
		
		return true;
	}


	private Timestamp CreateTimeStamp(int[] date, int time) {
		Timestamp stamp = new Timestamp(0);
		stamp.setYear(date[0]);
		stamp.setMonth(date[1] - 1);
		stamp.setDate(date[2]);
		stamp.setHours(time / 60);
		stamp.setMinutes(time % 60);
		return stamp;
	}

	private String checkValidUserId(String userId) {
		if(userController.checkUserExists(userId)) {
			JOptionPane.showMessageDialog(this, "Username is already registered", "Input Error", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		else if(ValidString(userId) == false) {
			JOptionPane.showMessageDialog(this, "Invalid username", "Input Error", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		else {
			return userId;
		}
	}
	
	private boolean ValidString(String s) {	
		if(s.isEmpty()) {
			return false;
		}
		
		char[] sChar = s.toCharArray();
		for(int i = 0; i < sChar.length; i++)
		{
			int sInt = (int)sChar[i];
			if(sInt < 48 || sInt > 122)
				return false;
			if(sInt > 57 && sInt < 65)
				return false;
			if(sInt > 90 && sInt < 97)
				return false;
		}
		return true;
	}

	private String checkValidPassword(String password, String rePassword) {
		if(password.equals(rePassword)) {
			return password;
		}
		else {
			JOptionPane.showMessageDialog(this, "Passwords do not match", "Input Error", JOptionPane.ERROR_MESSAGE);
			return null;
		}
	}

	private String checkValidEmail(String email) {
		Pattern regexPattern = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
		Matcher regMatcher	= regexPattern.matcher(email);
		if(regMatcher.matches()){
			return email;
		} else {
			JOptionPane.showMessageDialog(this, "Invalid email address", "Input Error", JOptionPane.ERROR_MESSAGE);
			return null;
		}
	}
	
	private String checkValidFirstName(String firstname) {

		if(firstname.matches("[a-zA-z]+([ '-][a-zA-Z]+)*")) {
			return firstname;
		}
		else {
			JOptionPane.showMessageDialog(this, "Invalid firstname", "Input Error", JOptionPane.ERROR_MESSAGE);
			return null;
		}
	}

	private String checkValidLastName(String lastname) {
		if(lastname.matches("[a-zA-z]+([ '-][a-zA-Z]+)*")) {
			return lastname;
		}
		else {
			JOptionPane.showMessageDialog(this, "Invalid lastname", "Input Error", JOptionPane.ERROR_MESSAGE);
			return null;
		}
	}
}
