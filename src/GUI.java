
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;

public class GUI extends JFrame {
    private JFrame loginFrame;
    private JFrame mainScreenFrame;
    private JTextField emailField;
    private JPasswordField passwordField;

    public void displayLoginPage() {
        loginFrame = new JFrame("IMDB Login");
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setPreferredSize(new Dimension(400, 400));

        JPanel panel = new JPanel();
        Color backgroundColor = new Color(135, 206, 250);
        panel.setBackground(backgroundColor);
        panel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel titleLabel = new JLabel("IMDB Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(backgroundColor);
        panel.add(titleLabel, gbc);

        JLabel emailLabel = new JLabel("Email:");
        emailField = new JTextField(15);
        panel.add(emailLabel, gbc);
        panel.add(emailField, gbc);

        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField(15);
        panel.add(passwordLabel, gbc);
        panel.add(passwordField, gbc);

        JButton loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(100, 40));
        panel.add(loginButton, gbc);

        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText();
                String password = new String(passwordField.getPassword());

                if (email.isEmpty()) {
                    showMessage("Email is empty", Color.RED);
                } else if (password.isEmpty()) {
                    showMessage("Password is empty", Color.RED);
                } else {
                    boolean loginSuccessful = false;
                    for (User user : IMDB.getInstance().users) {
                        if (user.userInformation.getUserCredentials().getEmail().equals(email) &&
                                user.userInformation.getUserCredentials().getPassword().equals(password)) {
                            loginSuccessful = true;
                            createMainScreen(user);
                            break;
                        }
                    }
                    if (!loginSuccessful) {
                        showMessage("Login failed", Color.RED);
                    }
                }
            }
        });

        JButton exitButton = new JButton("Exit");
        exitButton.setPreferredSize(new Dimension(100, 40));
        panel.add(exitButton, gbc);

        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                OverwriteJSON.updateUsers();
                OverwriteJSON.updateActors();
                OverwriteJSON.updateProductions();
                OverwriteJSON.updateRequests();
                System.exit(0);
            }
        });

        loginFrame.add(panel);
        loginFrame.pack();
        loginFrame.setLocationRelativeTo(null);
        loginFrame.setVisible(true);
    }

    public void displayActorDetailsSearch(User user, Actor actor) {
        JFrame actorDetailsFrame = new JFrame();
        actorDetailsFrame.setTitle("Actor Details: " + actor.name);
        actorDetailsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        actorDetailsFrame.setSize(600, 400);
        actorDetailsFrame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new GridLayout(1, 1));

        JPanel actorDetailsPanel = new JPanel();
        actorDetailsPanel.setLayout(new BoxLayout(actorDetailsPanel, BoxLayout.Y_AXIS));
        addLabelAndTextArea(actorDetailsPanel, "Actor name:", actor.name);
        addLabelAndTextArea(actorDetailsPanel, "Actor biography:", actor.biography);
        addLabelAndTextArea(actorDetailsPanel, "Actor pair list:", "");
        if (actor.pairList == null) {
            addLabelAndTextArea(actorDetailsPanel, "\tProduction Type:", "None");
            addLabelAndTextArea(actorDetailsPanel, "\tProduction Name:", "None");
        } else {
            for (Pair<String, ProductionType> pair : actor.pairList) {
                addLabelAndTextArea(actorDetailsPanel, "\tProduction Type:", pair.productionType.toString());
                addLabelAndTextArea(actorDetailsPanel, "\tProduction Name:", pair.name);
            }
        }

        JScrollPane scrollPane = new JScrollPane(actorDetailsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        mainPanel.add(scrollPane);

        JPanel actorImagePanel = new JPanel();
        String path = "C:\\Users\\sandu\\IdeaProjects\\Tema1_POO_IMDB\\assets\\actors\\" + actor.name + ".jpeg";
        ImageIcon actorImage = new ImageIcon(path);
        JLabel actorImageLabel = new JLabel(actorImage);
        actorImagePanel.add(actorImageLabel);

        mainPanel.add(actorImagePanel);

        actorDetailsFrame.setContentPane(mainPanel);
        actorDetailsFrame.setVisible(true);
    }
    public void displayChoices(User user) {
        JFrame choicesFrame = new JFrame();
        choicesFrame.setTitle("User Choices");
        choicesFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        choicesFrame.setSize(400, 400);
        choicesFrame.getContentPane().setBackground(new Color(135, 206, 250));

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(135, 206, 250));

        JLabel usernameLabel = new JLabel("Username: " + user.username);
        panel.add(usernameLabel);

        JLabel experienceLabel = new JLabel("Experience: " + user.userExperience);
        panel.add(experienceLabel);

        JButton[] buttons;
        Color buttonColor;
        if (user.userType.equals(AccountType.Admin)) {
            buttonColor = new Color(0, 102, 204);
            buttons = new JButton[]{
                    createStyledButton("View Productions Details", buttonColor),
                    createStyledButton("View Actors Details", buttonColor),
                    createStyledButton("View Notifications", buttonColor),
                    createStyledButton("Search Actor/Movie/Series", buttonColor),
                    createStyledButton("Add/Delete Actor/Movie/Series to/from Favorites", buttonColor),
                    createStyledButton("Add/Delete Actor/Movie/Series to/from System", buttonColor),
                    createStyledButton("View/Solve Requests", buttonColor),
                    createStyledButton("Update Production/Actor Details", buttonColor),
                    createStyledButton("Add/Delete User from System", buttonColor),
                    createStyledButton("Logout", buttonColor)
            };
        } else if (user.userType.equals(AccountType.Contributor)) {
            buttonColor = new Color(255, 165, 0);
            buttons = new JButton[]{
                    createStyledButton("View Productions Details", buttonColor),
                    createStyledButton("View Actors Details", buttonColor),
                    createStyledButton("View Notifications", buttonColor),
                    createStyledButton("Search Actor/Movie/Series", buttonColor),
                    createStyledButton("Add/Delete actor/production to/from favorites", buttonColor),
                    createStyledButton("Create/Delete a request", buttonColor),
                    createStyledButton("Add/Delete actor/production to/from system", buttonColor),
                    createStyledButton("View/Solve requests", buttonColor),
                    createStyledButton("Update Production/Actor details", buttonColor),
                    createStyledButton("Logout", buttonColor)
            };
        } else {
            buttonColor = new Color(50, 205, 50);
            buttons = new JButton[]{
                    createStyledButton("View Productions Details", buttonColor),
                    createStyledButton("View Actors Details", buttonColor),
                    createStyledButton("View Notifications", buttonColor),
                    createStyledButton("Search Actor/Movie/Series", buttonColor),
                    createStyledButton("Add/Delete actor/production to/from favorites", buttonColor),
                    createStyledButton("Create/Delete a request", buttonColor),
                    createStyledButton("Add/Delete rating to/from production", buttonColor),
                    createStyledButton("Logout", buttonColor)
            };
        }

        for (JButton button : buttons) {
            panel.add(button);
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JButton clickedButton = (JButton) e.getSource();
                    String buttonText = clickedButton.getText();
                    if(user.userType.equals(AccountType.Regular)) {
                        regularLogic(user, buttons, buttonText);
                    } else if(user.userType.equals(AccountType.Contributor)) {
                        contributorLogic(user, buttons, buttonText);
                    } else if(user.userType.equals(AccountType.Admin)) {
                        adminLogic(user, buttons, buttonText);
                    }
                }
            });
        }

        setButtonSizes(buttons);

        choicesFrame.add(panel);
        choicesFrame.setLocationRelativeTo(null);
        choicesFrame.setVisible(true);
    }

    public void createMainScreen(User user) {
        JFrame frame = new JFrame("IMDB");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 800);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(new Color(135, 206, 250));
        JButton menuButton = new JButton("Menu");
        JPanel menuPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        menuPanel.setOpaque(false);
        menuPanel.add(menuButton);
        menuButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                displayChoices(user);
            }
        });

        JPanel recommendationsPanel = new JPanel(new GridLayout(8, 1));
        ArrayList<Production> sortedProductions = new ArrayList<>();
        for (Production production : IMDB.getInstance().productions) {
            sortedProductions.add(production);
        }
        Collections.sort(sortedProductions, Collections.reverseOrder());
        for (int i = 0; i < 8; i++) {
            JLabel titleLabel = new JLabel(sortedProductions.get(i).productionTitle);
            String path;
            if (sortedProductions.get(i).productionTitle.equals("The Lord of the Rings: The Return of the King")) {
                path = "C:\\Users\\sandu\\IdeaProjects\\Tema1_POO_IMDB\\assets\\productions\\The Lord of the Rings.jpeg";
            } else if (sortedProductions.get(i).productionTitle.equals("Mad Max: Fury Road")) {
                path = "C:\\Users\\sandu\\IdeaProjects\\Tema1_POO_IMDB\\assets\\productions\\Mad Max.jpeg";
            } else {
                path = "C:\\Users\\sandu\\IdeaProjects\\Tema1_POO_IMDB\\assets\\productions\\" + sortedProductions.get(i).productionTitle + ".jpeg";
            }
            ImageIcon productionImage = new ImageIcon(path);
            JLabel imageLabel = new JLabel(productionImage);
            recommendationsPanel.add(titleLabel);
            recommendationsPanel.add(imageLabel);
        }
        JScrollPane recommendationsScrollPane = new JScrollPane(recommendationsPanel);
        recommendationsScrollPane.getViewport().setBackground(new Color(135, 206, 250));
        recommendationsScrollPane.setBorder(BorderFactory.createEmptyBorder());
        frame.add(menuPanel, BorderLayout.NORTH);
        frame.add(recommendationsScrollPane, BorderLayout.CENTER);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        return button;
    }

    private void setButtonSizes(JButton[] buttons) {
        int maxWidth = 0;
        for (JButton button : buttons) {
            int width = button.getPreferredSize().width;
            maxWidth = Math.max(maxWidth, width);
        }
        Dimension maxDimension = new Dimension(maxWidth, buttons[0].getPreferredSize().height);
        for (JButton button : buttons) {
            button.setPreferredSize(maxDimension);
        }
    }

    public void regularLogic(User user, JButton[] buttons, String buttonText) {
        if (buttonText.equals("View Productions Details")) {
            displayProductionsDetails(user);
        } else if (buttonText.equals("View Actors Details")) {
            displayActorsDetails(user, IMDB.getInstance().actors);
        } else if (buttonText.equals("View Notifications")) {
            displayNotifications(user);
        } else if (buttonText.equals("Search Actor/Movie/Series")) {
            displaySearch(user);
        } else if (buttonText.equals("Add/Delete actor/production to/from favorites")) {
            displayFavorites(user);
        } else if (buttonText.equals("Create/Delete a request")) {
            displayCreateDeleteRequest(user);
        } else if (buttonText.equals("Add/Delete rating to/from production")) {
            displayAddDeleteRating(user);
        } else if (buttonText.equals("Logout")) {
            dispose();
            displayLoginPage();
        }
    }

    public void contributorLogic(User user, JButton[] buttons, String buttonText) {
        if (buttonText.equals("View Productions Details")) {
            displayProductionsDetails(user);
        } else if (buttonText.equals("View Actors Details")) {
            displayActorsDetails(user, IMDB.getInstance().actors);
        } else if (buttonText.equals("View Notifications")) {
            displayNotifications(user);
        } else if (buttonText.equals("Search Actor/Movie/Series")) {
            displaySearch(user);
        } else if (buttonText.equals("Add/Delete actor/production to/from favorites")) {
            displayFavorites(user);
        } else if (buttonText.equals("Create/Delete a request")) {
            displayCreateDeleteRequest(user);
        } else if (buttonText.equals("Add/Delete actor/production to/from system")) {
            displayAddDelete(user);
        } else if (buttonText.equals("View/Solve requests")) {
            displayRequests(user);
        } else if (buttonText.equals("Update Production/Actor details")) {
            displayUpdate(user);
        } else if (buttonText.equals("Logout")) {
            dispose();
            displayLoginPage();
        }
    }

    public void adminLogic(User user, JButton[] buttons, String buttonText) {
        if (buttonText.equals("View Productions Details")) {
            displayProductionsDetails(user);
        } else if (buttonText.equals("View Actors Details")) {
            displayActorsDetails(user, IMDB.getInstance().actors);
        } else if (buttonText.equals("View Notifications")) {
            displayNotifications(user);
        } else if (buttonText.equals("Search Actor/Movie/Series")) {
            displaySearch(user);
        } else if (buttonText.equals("Add/Delete Actor/Movie/Series to/from Favorites")) {
            displayFavorites(user);
        } else if (buttonText.equals("Add/Delete Actor/Movie/Series to/from System")) {
            displayAddDelete(user);
        } else if (buttonText.equals("View/Solve Requests")) {
            displayRequests(user);
        } else if (buttonText.equals("Update Production/Actor Details")) {
            displayUpdate(user);
        } else if (buttonText.equals("Add/Delete User from System")) {
            displayAddDeleteUser(user);
        } else if (buttonText.equals("Logout")) {
            dispose();
            displayLoginPage();
        }
    }

    public void displayAddDeleteUser(User user) {
        JFrame addDeleteUserFrame = new JFrame("Add/Delete User");
        addDeleteUserFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();

        JButton addUserButton = new JButton("Add User");
        addUserButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayAddUser(user);
            }
        });

        JButton deleteUserButton = new JButton("Delete User");
        deleteUserButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayDeleteUser(user);
            }
        });

        JButton backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addDeleteUserFrame.dispose();
                displayChoices(user);
            }
        });

        panel.add(addUserButton);
        panel.add(deleteUserButton);
        panel.add(backButton);

        addDeleteUserFrame.add(panel);
        addDeleteUserFrame.setSize(300, 150);
        addDeleteUserFrame.setLocationRelativeTo(null);
        addDeleteUserFrame.setVisible(true);
    }

    public void displayAddUser(User user) {
        JFrame addUserFrame = new JFrame("Add User");
        addUserFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("Choose User Type:");
        panel.add(titleLabel);

        JButton regularButton = new JButton("Regular");
        regularButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayUserDataEntry(user, "Regular");
                addUserFrame.dispose();
            }
        });
        panel.add(regularButton);

        JButton contributorButton = new JButton("Contributor");
        contributorButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayUserDataEntry(user, "Contributor");
                addUserFrame.dispose();
            }
        });
        panel.add(contributorButton);

        JButton adminButton = new JButton("Admin");
        adminButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayUserDataEntry(user, "Admin");
                addUserFrame.dispose();
            }
        });
        panel.add(adminButton);

        addUserFrame.add(panel);
        addUserFrame.setSize(300, 150);
        addUserFrame.setLocationRelativeTo(null);
        addUserFrame.setVisible(true);
    }

    public void displayUserDataEntry(User user, String userType) {
        JFrame dataEntryFrame = new JFrame("User Data Entry");
        dataEntryFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel userTypeLabel = new JLabel("User Type: " + userType);
        panel.add(userTypeLabel);

        JTextField fullNameField = new JTextField(20);
        fullNameField.setText("User's Full Name");
        fullNameField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (fullNameField.getText().equals("User's Full Name")) {
                    fullNameField.setText("");
                }
            }

            public void focusLost(java.awt.event.FocusEvent evt) {
                if (fullNameField.getText().isEmpty()) {
                    fullNameField.setText("User's Full Name");
                }
            }
        });
        panel.add(fullNameField);

        JTextField countryField = new JTextField(20);
        countryField.setText("User's Country");
        countryField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (countryField.getText().equals("User's Country")) {
                    countryField.setText("");
                }
            }

            public void focusLost(java.awt.event.FocusEvent evt) {
                if (countryField.getText().isEmpty()) {
                    countryField.setText("User's Country");
                }
            }
        });
        panel.add(countryField);

        JTextField ageField = new JTextField(20);
        ageField.setText("User's Age");
        ageField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (ageField.getText().equals("User's Age")) {
                    ageField.setText("");
                }
            }

            public void focusLost(java.awt.event.FocusEvent evt) {
                if (ageField.getText().isEmpty()) {
                    ageField.setText("User's Age");
                }
            }
        });
        panel.add(ageField);

        JTextField birthdayField = new JTextField(20);
        birthdayField.setText("User's Birthday (yyyy-mm-dd)");
        birthdayField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (birthdayField.getText().equals("User's Birthday (yyyy-mm-dd)")) {
                    birthdayField.setText("");
                }
            }

            public void focusLost(java.awt.event.FocusEvent evt) {
                if (birthdayField.getText().isEmpty()) {
                    birthdayField.setText("User's Birthday (yyyy-mm-dd)");
                }
            }
        });
        panel.add(birthdayField);

        JTextField emailField = new JTextField(20);
        emailField.setText("User's Email");
        emailField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (emailField.getText().equals("User's Email")) {
                    emailField.setText("");
                }
            }

            public void focusLost(java.awt.event.FocusEvent evt) {
                if (emailField.getText().isEmpty()) {
                    emailField.setText("User's Email");
                }
            }
        });
        panel.add(emailField);

        JButton doneButton = new JButton("Done");
        doneButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String fullName = fullNameField.getText();
                String country = countryField.getText();
                int age;
                try {
                    age = Integer.parseInt(ageField.getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dataEntryFrame, "Age must be a number!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String birthday = birthdayField.getText();
                String email = emailField.getText();
                if(fullName.equals("User's Full Name") || country.equals("User's Country") || ageField.getText().equals("User's Age") || birthday.equals("User's Birthday (yyyy-mm-dd)") || email.equals("User's Email")) {
                    JOptionPane.showMessageDialog(dataEntryFrame, "Please fill in all the fields!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String userPassword = UsernamePasswordGenerator.generatePassword();
                boolean isUnique;
                String username;
                while(true) {
                    try {
                        username = UsernamePasswordGenerator.generateUsername(fullName);
                    } catch (InformationIncompleteException ex) {
                        throw new RuntimeException(ex);
                    }
                    isUnique = true;
                    for(User user1 : IMDB.getInstance().users) {
                        if(user1.username.equals(username)) {
                            isUnique = false;
                            break;
                        }
                    }
                    if(isUnique) {
                        break;
                    }
                }
                User newUser = UserFactory.createUser(null, AccountType.valueOf(userType), 0, username);
                User.InformationBuilder builder = new User.InformationBuilder();
                newUser.userInformation = builder
                        .credentials(new Credentials(email, userPassword))
                        .userName(fullName)
                        .userCountry(country)
                        .userAge(age)
                        .birthDate(LocalDate.parse(birthday))
                        .build(newUser);
                newUser.userExperience = 0;
                IMDB.getInstance().users.add(newUser);
                displayCredentials(newUser);
                dataEntryFrame.dispose();
            }
        });
        panel.add(doneButton);

        dataEntryFrame.add(panel);
        dataEntryFrame.setSize(400, 250);
        dataEntryFrame.setLocationRelativeTo(null);
        dataEntryFrame.setVisible(true);
    }

    public void displayDeleteUser(User user) {
        JFrame deleteUserFrame = new JFrame("Delete User");
        deleteUserFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel nameLabel = new JLabel("Enter User's Name to Delete:");
        panel.add(nameLabel);

        JTextField nameField = new JTextField();
        panel.add(nameField);

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String userNameToDelete = nameField.getText();
                User user1 = null;
                for(User user2 : IMDB.getInstance().users) {
                    if(user2.username.equals(userNameToDelete)) {
                        user1 = user2;
                        break;
                    }
                }
                if(user1 == null) {
                    System.out.println("User not found!");
                } else {
                    if (user1.userType.equals(AccountType.Contributor)) {
                        for (Request request : IMDB.getInstance().requests) {
                            if (request.solverUsername.equals(user1.username)) {
                                request.solverUsername = "Admin";
                            }
                        }
                        for (Production production : IMDB.getInstance().productions) {
                            if (production.inserterUsername.equals(user1.username)) {
                                production.inserterUsername = "Admin";
                            }
                        }
                        for (Actor actor : IMDB.getInstance().actors) {
                            if (actor.inserterUsername.equals(user1.username)) {
                                actor.inserterUsername = "Admin";
                            }
                        }
                    }
                    IMDB.getInstance().users.remove(user1);
                    JOptionPane.showMessageDialog(deleteUserFrame, "User deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    deleteUserFrame.dispose();
                }
            }
        });
        panel.add(deleteButton);

        deleteUserFrame.add(panel);
        deleteUserFrame.setSize(300, 150);
        deleteUserFrame.setLocationRelativeTo(null);
        deleteUserFrame.setVisible(true);
    }

    public void displayCredentials(User user) {
        JFrame credentialsFrame = new JFrame("New User Credentials");
        credentialsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("New User Credentials:");
        panel.add(titleLabel);

        JTextArea credentialsTextArea = new JTextArea();
        credentialsTextArea.setEditable(false);
        credentialsTextArea.setWrapStyleWord(true);
        credentialsTextArea.setLineWrap(true);
        credentialsTextArea.setText("Username: " + user.username + "\nPassword: " + user.userInformation.getUserCredentials().getPassword());
        panel.add(new JScrollPane(credentialsTextArea));

        credentialsFrame.add(panel);
        credentialsFrame.setSize(300, 150);
        credentialsFrame.setLocationRelativeTo(null);
        credentialsFrame.setVisible(true);
    }

    public void displayUpdate(User user) {
        JFrame updateFrame = new JFrame("Update Details");
        updateFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();

        JButton updateActorButton = new JButton("Update Actor");
        updateActorButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayUpdateActor(user);
            }
        });

        JButton updateMovieButton = new JButton("Update Movie");
        updateMovieButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayUpdateMovie(user);
            }
        });

        JButton updateSeriesButton = new JButton("Update Series");
        updateSeriesButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayUpdateSeries(user);
            }
        });

        JButton backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateFrame.dispose();
                displayChoices(user);
            }
        });

        panel.add(updateActorButton);
        panel.add(updateMovieButton);
        panel.add(updateSeriesButton);
        panel.add(backButton);

        updateFrame.add(panel);
        updateFrame.setSize(300, 150);
        updateFrame.setLocationRelativeTo(null);
        updateFrame.setVisible(true);
    }

    private void displayUpdateActor(User user) {
        JFrame actorFrame = new JFrame("Update Actor");
        JPanel panel = new JPanel();

        JTextField actorNameField = new JTextField(20);
        JTextField updateNameField = new JTextField(20);
        JTextField updateBioField = new JTextField(20);

        JButton updateButton = new JButton("Update");
        updateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Actor actor = null;
                for(Actor actor1 : IMDB.getInstance().actors) {
                    if(actor1.name.equals(actorNameField.getText())) {
                        actor = actor1;
                        break;
                    }
                }
                if(actor == null) {
                    JOptionPane.showMessageDialog(actorFrame, "Actor not found!", "Error", JOptionPane.ERROR_MESSAGE);
                } else if(actor.inserterUsername.equals(user.username) || (actor.inserterUsername.equals(AccountType.Admin.toString()) && user.userType.equals(AccountType.Admin))) {
                    if(!updateNameField.getText().isEmpty()) {
                        actor.name = updateNameField.getText();
                    }
                    if(!updateBioField.getText().isEmpty()) {
                        actor.biography = updateBioField.getText();
                    }

                    JOptionPane.showMessageDialog(actorFrame, "Actor updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(actorFrame, "You cannot update an actor you did not add!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        panel.add(new JLabel("Actor Name:"));
        panel.add(actorNameField);
        panel.add(new JLabel("Update Name:"));
        panel.add(updateNameField);
        panel.add(new JLabel("Update Biography:"));
        panel.add(updateBioField);
        panel.add(updateButton);

        actorFrame.add(panel);
        actorFrame.setSize(300, 200);
        actorFrame.setLocationRelativeTo(null);
        actorFrame.setVisible(true);
    }

    private void displayUpdateMovie(User user) {
        JFrame movieFrame = new JFrame("Update Movie");
        JPanel panel = new JPanel();

        JTextField movieNameField = new JTextField(20);
        JTextField updatedTitleField = new JTextField(20);
        JTextField updatedPlotField = new JTextField(20);
        JTextField updatedDurationField = new JTextField(20);
        JTextField updatedReleaseYearField = new JTextField(20);

        JButton updateButton = new JButton("Update");
        updateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Movie movie = null;
                for(Production production : IMDB.getInstance().productions) {
                    if(production instanceof Movie && production.productionTitle.equals(movieNameField.getText())) {
                        movie = (Movie) production;
                        break;
                    }
                }
                if(movie == null) {
                    JOptionPane.showMessageDialog(movieFrame, "Movie not found!", "Error", JOptionPane.ERROR_MESSAGE);
                } else if(movie.inserterUsername.equals(user.username)) {
                    if(!updatedTitleField.getText().isEmpty()) {
                        movie.movieName = updatedTitleField.getText();
                    }
                    if(!updatedPlotField.getText().isEmpty()) {
                        movie.movieDescription = updatedPlotField.getText();
                    }
                    if(!updatedDurationField.getText().isEmpty()) {
                        movie.movieDuration = updatedDurationField.getText();
                    }
                    if(!updatedReleaseYearField.getText().isEmpty()) {
                        movie.releaseYear = Integer.parseInt(updatedReleaseYearField.getText());
                    }
                } else {
                    System.out.println("You cannot update this movie!");
                }
            }
        });

        panel.add(new JLabel("Movie Name:"));
        panel.add(movieNameField);
        panel.add(new JLabel("Updated Title:"));
        panel.add(updatedTitleField);
        panel.add(new JLabel("Updated Plot:"));
        panel.add(updatedPlotField);
        panel.add(new JLabel("Updated Duration:"));
        panel.add(updatedDurationField);
        panel.add(new JLabel("Updated Release Year:"));
        panel.add(updatedReleaseYearField);
        panel.add(updateButton);

        movieFrame.add(panel);
        movieFrame.setSize(350, 250);
        movieFrame.setLocationRelativeTo(null);
        movieFrame.setVisible(true);
    }

    private void displayUpdateSeries(User user) {
        JFrame seriesFrame = new JFrame("Update Series");
        JPanel panel = new JPanel();

        JTextField updatedTitleField = new JTextField(20);
        JTextField updatedPlotField = new JTextField(20);
        JTextField updatedReleaseYearField = new JTextField(20);

        JButton updateButton = new JButton("Update");
        updateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Series series = null;
                for(Production production : IMDB.getInstance().productions) {
                    if(production instanceof Series) {
                        series = (Series) production;
                        break;
                    }
                }
                if(series == null) {
                    JOptionPane.showMessageDialog(seriesFrame, "Series not found!", "Error", JOptionPane.ERROR_MESSAGE);
                } else if(series.inserterUsername.equals(user.username)) {
                    if(!updatedTitleField.getText().isEmpty()) {
                        series.productionTitle = updatedTitleField.getText();
                    }
                    if(!updatedPlotField.getText().isEmpty()) {
                        series.productionTitle = updatedPlotField.getText();
                    }
                    if(!updatedReleaseYearField.getText().isEmpty()) {
                        series.releaseYear = Integer.parseInt(updatedReleaseYearField.getText());
                    }
                } else {
                    JOptionPane.showMessageDialog(seriesFrame, "You cannot update this series!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        panel.add(new JLabel("Updated Title:"));
        panel.add(updatedTitleField);
        panel.add(new JLabel("Updated Plot:"));
        panel.add(updatedPlotField);
        panel.add(new JLabel("Updated Release Year:"));
        panel.add(updatedReleaseYearField);
        panel.add(updateButton);

        seriesFrame.add(panel);
        seriesFrame.setSize(300, 200);
        seriesFrame.setLocationRelativeTo(null);
        seriesFrame.setVisible(true);
    }

    public void displayRequests(User user) {
        JFrame requestsFrame = new JFrame("Requests");
        requestsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();

        JButton viewRequestsButton = new JButton("View Requests");
        viewRequestsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showRequests(user);
            }
        });

        JButton solveRequestsButton = new JButton("Solve Requests");
        solveRequestsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displaySolveRequests(user);
            }
        });

        JButton backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                requestsFrame.dispose();
            }
        });

        panel.add(viewRequestsButton);
        panel.add(solveRequestsButton);
        panel.add(backButton);

        requestsFrame.add(panel);
        requestsFrame.setSize(300, 150);
        requestsFrame.setLocationRelativeTo(null);
        requestsFrame.setVisible(true);
    }

    private void showRequests(User user) {
        JFrame showRequestsFrame = new JFrame("Requests");
        showRequestsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel requestsLabel = new JLabel("Requests:");
        panel.add(requestsLabel);

        JTextArea requestsTextArea = new JTextArea(20, 40);
        requestsTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(requestsTextArea);
        panel.add(scrollPane);

        int i = 0;
        for (Request request : IMDB.getInstance().requests) {
            if (request.solverUsername.equals(user.username) || (request.solverUsername.equals(AccountType.Admin.toString()) && user.userType.equals(AccountType.Admin))) {
                requestsTextArea.append("Request ID: " + i + "\n");
                if(request.actorName != null) {
                    requestsTextArea.append("\tActor Name: " + request.actorName + "\n");
                } else if(request.productionTitle != null) {
                    requestsTextArea.append("\tProduction Name: " + request.productionTitle + "\n");
                }
                requestsTextArea.append("\tRequest Description: " + request.problemDescription + "\n");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                String formatDateTime = request.getRequestDate().format(formatter);
                requestsTextArea.append("\tRequest Date: " + formatDateTime + "\n");
                i++;
            }
        }

        showRequestsFrame.add(panel);
        showRequestsFrame.setSize(300, 300);
        showRequestsFrame.setLocationRelativeTo(null);
        showRequestsFrame.setVisible(true);
    }

    private void displaySolveRequests(User user) {
        JFrame solveRequestsFrame = new JFrame("Solve Requests");
        JPanel panel = new JPanel();

        JTextField solveField = new JTextField(10);
        JTextField declineField = new JTextField(10);
        JButton solveButton = new JButton("Solve Request");
        solveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int requestId;
                try {
                    requestId = Integer.parseInt(solveField.getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(solveRequestsFrame, "Please enter a valid request ID!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                int i = 0;
                Request request = null;
                for(Request request1 : IMDB.getInstance().requests) {
                    System.out.println(request1.solverUsername);
                    if(request1.solverUsername.equals(user.username)) {
                        if(i == requestId) {
                            request = request1;
                            break;
                        }
                        i++;
                    }
                }
                if(request == null) {
                    JOptionPane.showMessageDialog(solveRequestsFrame, "Request not found!", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    IMDB.getInstance().requests.remove(request);
                    if (request.getRequestType().equals(RequestType.ACTOR_ISSUE)) {
                        User user1 = null;
                        for (User user2 : IMDB.getInstance().users) {
                            if (user2.username.equals(request.requesterUsername)) {
                                user1 = user2;
                                break;
                            }
                        }
                        RequestExperience requestExperience = new RequestExperience();
                        user1.userExperience = requestExperience.calculateExperience(user1);
                        request.notifyObservers("Your request regarding " + request.actorName + " has been solved!", 1);
                    } else {
                        User user1 = null;
                        for (User user2 : IMDB.getInstance().users) {
                            if (user2.username.equals(request.requesterUsername)) {
                                user1 = user2;
                                break;
                            }
                        }
                        RequestExperience requestExperience = new RequestExperience();
                        user1.userExperience = requestExperience.calculateExperience(user1);
                        request.notifyObservers("Your request regarding " + request.productionTitle + " has been solved!", 1);
                    }
                    JOptionPane.showMessageDialog(solveRequestsFrame, "Request solved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        JButton declineButton = new JButton("Decline Request");
        declineButton.addActionListener(new ActionListener() {
            int requestId;
            public void actionPerformed(ActionEvent e) {
                try {
                    requestId = Integer.parseInt(declineField.getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(solveRequestsFrame, "Please enter a valid request ID!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                int i = 0;
                Request request = null;
                for(Request request1 : IMDB.getInstance().requests) {
                    if(request1.solverUsername.equals(user.username)) {
                        if(i == requestId) {
                            request = request1;
                            break;
                        }
                        i++;
                    }
                }
                if(request == null) {
                    JOptionPane.showMessageDialog(solveRequestsFrame, "Request not found!", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    IMDB.getInstance().requests.remove(request);
                    request.notifyObservers("Your request has been declined!", 1);
                    JOptionPane.showMessageDialog(solveRequestsFrame, "Request declined successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        panel.add(new JLabel("Enter ID to Solve:"));
        panel.add(solveField);
        panel.add(solveButton);

        panel.add(new JLabel("Enter ID to Decline:"));
        panel.add(declineField);
        panel.add(declineButton);


        solveRequestsFrame.add(panel);
        solveRequestsFrame.setSize(250, 150);
        solveRequestsFrame.setLocationRelativeTo(null);
        solveRequestsFrame.setVisible(true);
    }

    public void deleteActor(User user, JFrame mainFrame) {
        JFrame deleteActorFrame = new JFrame("Delete Actor");
        deleteActorFrame.setLayout(new GridLayout(3, 1));

        JTextField actorNameField = new JTextField(20);
        JButton doneButton = new JButton("Done");

        doneButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int found = 0;
                for(Actor actor : IMDB.getInstance().actors) {
                    if(actor.name.equals(actorNameField.getText())) {
                        if(actor.inserterUsername.equals(user.username) || (actor.inserterUsername.equals(AccountType.Admin.toString()) && user.userType.equals(AccountType.Admin))) {
                            IMDB.getInstance().actors.remove(actor);
                            ((Staff)user).addedActorsAndProductions.remove(actor);
                            found = 1;
                            for(User user1 : IMDB.getInstance().users) {
                                if(user1.userPreferences.contains(actor)) {
                                    user1.userPreferences.remove(actor);
                                }
                            }
                            break;
                        } else {
                            JOptionPane.showMessageDialog(deleteActorFrame, "You cannot delete an actor you did not add!", "Error", JOptionPane.ERROR_MESSAGE);
                            break;
                        }
                    }
                }
                if (found == 0) {
                    JOptionPane.showMessageDialog(deleteActorFrame, "Actor not found!", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(deleteActorFrame, "Actor deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        deleteActorFrame.add(new JLabel("Actor Name:"));
        deleteActorFrame.add(actorNameField);
        deleteActorFrame.add(doneButton);

        deleteActorFrame.setSize(400, 150);
        deleteActorFrame.setLocationRelativeTo(null);
        deleteActorFrame.setVisible(true);
    }

    public void deleteProduction(User user, JFrame mainFrame) {
        JFrame deleteProductionFrame = new JFrame("Delete Production");
        deleteProductionFrame.setLayout(new GridLayout(3, 1));

        JTextField productionNameField = new JTextField(20);
        JButton doneButton = new JButton("Done");

        doneButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int found = 0;
                for(Production production : IMDB.getInstance().productions) {
                    if(production.productionTitle.equals(productionNameField.getText())) {
                        if(production.inserterUsername.equals(user.username) || (production.inserterUsername.equals(AccountType.Admin.toString()) && user.userType.equals(AccountType.Admin))) {
                            IMDB.getInstance().productions.remove(production);
                            ((Staff)user).addedActorsAndProductions.remove(production);
                            found = 1;
                            for(User user1 : IMDB.getInstance().users) {
                                if(user1.userPreferences.contains(production)) {
                                    user1.userPreferences.remove(production);
                                }
                            }
                            break;
                        } else {
                            JOptionPane.showMessageDialog(deleteProductionFrame, "You cannot delete a production you did not add!", "Error", JOptionPane.ERROR_MESSAGE);
                            break;
                        }
                    }
                }
                if (found == 0) {
                    JOptionPane.showMessageDialog(deleteProductionFrame, "Production not found!", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(deleteProductionFrame, "Production deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        deleteProductionFrame.add(new JLabel("Production Name:"));
        deleteProductionFrame.add(productionNameField);
        deleteProductionFrame.add(doneButton);

        deleteProductionFrame.setSize(400, 150);
        deleteProductionFrame.setLocationRelativeTo(null);
        deleteProductionFrame.setVisible(true);
    }

    public void addProduction(User user, JFrame mainFrame) {
        JFrame addProductionFrame = new JFrame("Add Production");
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JTextField productionNameField = new JTextField(20);
        JTextField descriptionField = new JTextField(20);
        JTextField productionTypeField = new JTextField(20);
        JTextField releaseYearField = new JTextField(20);
        JTextField durationField = new JTextField(20);
        JButton sendButton = new JButton("Send");

        sendButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(new JLabel("Production Name:"));
        panel.add(productionNameField);
        panel.add(new JLabel("Description:"));
        panel.add(descriptionField);
        panel.add(new JLabel("Production Type:"));
        panel.add(productionTypeField);
        panel.add(new JLabel("Release Year:"));
        panel.add(releaseYearField);
        panel.add(new JLabel("Duration:"));
        panel.add(durationField);
        panel.add(sendButton);

        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String productionName = productionNameField.getText();
                String description = descriptionField.getText();
                String productionType = productionTypeField.getText();
                String releaseYearText = releaseYearField.getText();
                String durationText = durationField.getText();
                int releaseYear;
                try {
                    releaseYear = Integer.parseInt(releaseYearText);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(addProductionFrame, "Please enter a valid release year!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                Production production = null;
                for (Production production1 : IMDB.getInstance().productions) {
                    if (production1.productionTitle.equals(productionName)) {
                        production = production1;
                        break;
                    }
                }
                if (production != null) {
                    JOptionPane.showMessageDialog(addProductionFrame, "Production already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    if(productionType.equals("Movie")) {
                        production = new Movie(productionName, description, 0, null, null, null, null, productionName, durationText, releaseYear);
                        production.inserterUsername = user.username;
                    } else if (productionType.equals("Series")) {
                        production = new Series(productionName, description, 0, null, null, null, null, releaseYear, 0, null);
                        production.inserterUsername = user.username;
                    } else {
                        JOptionPane.showMessageDialog(addProductionFrame, "Please enter a valid production type!", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    IMDB.getInstance().productions.add(production);
                    ((Staff)user).addedActorsAndProductions.add(production);
                    ProductionExperience productionExperience = new ProductionExperience();
                    user.userExperience = productionExperience.calculateExperience(user);
                    JOptionPane.showMessageDialog(addProductionFrame, "Production added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        addProductionFrame.add(panel);
        addProductionFrame.setSize(300, 300);
        addProductionFrame.setLocationRelativeTo(null);
        addProductionFrame.setVisible(true);
    }

    public void addActor(User user, JFrame mainFrame) {
        JFrame addActorFrame = new JFrame("Add Actor");
        addActorFrame.setLayout(new GridLayout(4, 1));

        JTextField actorNameField = new JTextField(20);
        JTextField descriptionField = new JTextField(20);
        JButton sendButton = new JButton("Send");

        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String actorName = actorNameField.getText();
                String description = descriptionField.getText();
                Actor actor = null;
                for (Actor actor1 : IMDB.getInstance().actors) {
                    if (actor1.name.equals(actorName)) {
                        actor = actor1;
                        break;
                    }
                }
                if (actor != null) {
                    JOptionPane.showMessageDialog(addActorFrame, "Actor already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    actor = new Actor(actorName, null, description, user.username);
                    actor.inserterUsername = user.username;
                    IMDB.getInstance().actors.add(actor);
                    ((Staff)user).addedActorsAndProductions.add(actor);
                    ActorExperience actorExperience = new ActorExperience();
                    user.userExperience = actorExperience.calculateExperience(user);
                    JOptionPane.showMessageDialog(addActorFrame, "Actor added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        addActorFrame.add(new JLabel("Actor Name:"));
        addActorFrame.add(actorNameField);
        addActorFrame.add(new JLabel("Description:"));
        addActorFrame.add(descriptionField);
        addActorFrame.add(sendButton);

        addActorFrame.setSize(400, 200);
        addActorFrame.setLocationRelativeTo(null);
        addActorFrame.setVisible(true);
    }

    public void displayAddDelete(User user) {
        JFrame mainFrame = new JFrame("Manage User Data");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(400, 300);
        mainFrame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(5, 1));

        JButton addActorButton = new JButton("Add Actor");
        addActorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addActor(user, mainFrame);
            }
        });

        JButton addProductionButton = new JButton("Add Production");
        addProductionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addProduction(user, mainFrame);
            }
        });

        JButton deleteActorButton = new JButton("Delete Actor");
        deleteActorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteActor(user, mainFrame);
            }
        });

        JButton deleteProductionButton = new JButton("Delete Production");
        deleteProductionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteProduction(user, mainFrame);
            }
        });

        JButton backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.dispose();
                displayChoices(user);
            }
        });

        mainPanel.add(addActorButton);
        mainPanel.add(addProductionButton);
        mainPanel.add(deleteActorButton);
        mainPanel.add(deleteProductionButton);
        mainPanel.add(backButton);

        mainFrame.add(mainPanel);
        mainFrame.setVisible(true);
    }

    public void displayAddDeleteRating(User user) {
        setTitle("Rating Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        JButton addRatingBtn = new JButton("Add Rating");
        JButton deleteRatingBtn = new JButton("Delete Rating");
        JButton backBtn = new JButton("Back");

        addRatingBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addRatingWindow(user);
            }
        });

        deleteRatingBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteRatingWindow(user);
            }
        });

        JButton backButton = new JButton("Back");
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.setForeground(Color.WHITE);
        backButton.setBackground(new Color(50, 205, 50));
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                displayChoices(user);
            }
        });

        add(backButton);
        add(addRatingBtn);
        add(deleteRatingBtn);

        setSize(400, 150);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void addRatingWindow(User user) {
        JFrame addRatingFrame = new JFrame("Add Rating");
        addRatingFrame.setLayout(new GridLayout(5, 1));

        JTextField productionNameField = new JTextField(20);
        JTextField ratingField = new JTextField(20);
        JTextField commentField = new JTextField(20);
        JButton sendButton = new JButton("Send");

        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String productionName = productionNameField.getText();
                int grade= Integer.parseInt(ratingField.getText());
                String comment = commentField.getText();

                Production production = null;
                for (Production production1 : IMDB.getInstance().productions) {
                    if (production1.productionTitle.equals(productionName)) {
                        production = production1;
                        break;
                    }
                }
                if (production == null) {
                    JOptionPane.showMessageDialog(addRatingFrame, "Production not found!", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    IMDB.getInstance().recalculateAverageProductionRating(production);
                    Rating rating = new Rating(user.username, grade, comment);
                    rating.registerObserver(user);
                    for(Rating rating1 : production.ratings) {
                        for(User user1 : IMDB.getInstance().users) {
                            if(user1.username.equals(rating1.user)) {
                                rating1.registerObserver(user1);
                                rating1.notifyObservers("A new rating has been added to " + production.productionTitle + "!", 1);
                                break;
                            }
                        }
                    }
                    production.ratings.add(rating);
                    IMDB.getInstance().recalculateAverageProductionRating(production);
                    boolean givenRating = false;
                    for(Object s1 : user.givenRating) {
                        if(((String)s1).equals(production.productionTitle)) {
                            givenRating = true;
                            break;
                        }
                    }
                    if(!givenRating) {
                        RatingExperience ratingExperience = new RatingExperience();
                        user.userExperience = ratingExperience.calculateExperience(user);
                        user.givenRating.add(production.productionTitle);
                    }
                    JOptionPane.showMessageDialog(addRatingFrame, "Rating added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        addRatingFrame.add(new JLabel("Production Name:"));
        addRatingFrame.add(productionNameField);
        addRatingFrame.add(new JLabel("Rating:"));
        addRatingFrame.add(ratingField);
        addRatingFrame.add(new JLabel("Comment:"));
        addRatingFrame.add(commentField);
        addRatingFrame.add(sendButton);

        addRatingFrame.setSize(400, 200);
        addRatingFrame.setLocationRelativeTo(null);
        addRatingFrame.setVisible(true);
    }

    private void deleteRatingWindow(User user) {
        JFrame deleteRatingFrame = new JFrame("Delete Rating");
        deleteRatingFrame.setLayout(new GridLayout(3, 1));

        JTextField productionNameField = new JTextField(20);
        JButton doneButton = new JButton("Done");

        doneButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int found = 0;
                String productionName = productionNameField.getText();
                Production production = null;
                for (Production production1 : IMDB.getInstance().productions) {
                    if (production1.productionTitle.equals(productionName)) {
                        production = production1;
                        break;
                    }
                }
                if (production == null) {
                    JOptionPane.showMessageDialog(deleteRatingFrame, "Production not found!", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    int i = 0;
                    for (Rating rating : production.ratings) {
                        if (rating.user.equals(user.username)) {
                            production.ratings.remove(rating);
                            found = 1;
                            IMDB.getInstance().recalculateAverageProductionRating(production);
                            break;
                        }
                        i++;
                    }
                    if (found == 0) {
                        JOptionPane.showMessageDialog(deleteRatingFrame, "Rating not found!", "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(deleteRatingFrame, "Rating deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        });

        deleteRatingFrame.add(new JLabel("Production Name:"));
        deleteRatingFrame.add(productionNameField);
        deleteRatingFrame.add(doneButton);

        deleteRatingFrame.setSize(400, 150);
        deleteRatingFrame.setLocationRelativeTo(null);
        deleteRatingFrame.setVisible(true);
    }


    public void displayCreateDeleteRequest(User user) {
        JFrame mainFrame = new JFrame();

        mainFrame.setTitle("Request Management");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLayout(new FlowLayout());

        JButton createRequestBtn = new JButton("Create a Request");
        createRequestBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createRequestWindow(user);
            }
        });

        JButton deleteRequestBtn = new JButton("Delete a Request");
        deleteRequestBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteRequestWindow();
            }
        });

        JButton viewRequestsBtn = new JButton("View All Requests");
        viewRequestsBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                viewRequestsWindow();
            }
        });

        JButton backButton = new JButton("Back");
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.setForeground(Color.WHITE);
        backButton.setBackground(new Color(50, 205, 50));
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mainFrame.dispose();
                displayChoices(user);
            }
        });

        mainFrame.add(backButton);
        mainFrame.add(createRequestBtn);
        mainFrame.add(deleteRequestBtn);
        mainFrame.add(viewRequestsBtn);

        mainFrame.setSize(400, 150);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }

    private void createRequestWindow(User user) {
        JFrame createRequestFrame = new JFrame("Create a Request");
        createRequestFrame.setLayout(new FlowLayout());

        JButton actorRequestBtn = new JButton("Create a request for an Actor");
        JButton productionRequestBtn = new JButton("Create a request for a Production");
        JButton deleteAccountBtn = new JButton("Delete my Account");
        JButton otherRequestBtn = new JButton("Other");

        actorRequestBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createActorRequest(user);
            }
        });

        productionRequestBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createProductionRequest(user);
            }
        });

        deleteAccountBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteAccountRequest(user);
            }
        });

        otherRequestBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                otherRequest(user);
            }
        });

        createRequestFrame.add(actorRequestBtn);
        createRequestFrame.add(productionRequestBtn);
        createRequestFrame.add(deleteAccountBtn);
        createRequestFrame.add(otherRequestBtn);

        createRequestFrame.setSize(400, 150);
        createRequestFrame.setLocationRelativeTo(null);
        createRequestFrame.setVisible(true);
    }


    private void createActorRequest(User user) {
        JFrame actorRequestFrame = new JFrame("Create Actor Request");
        actorRequestFrame.setLayout(new GridLayout(4, 1));

        JTextField actorNameField = new JTextField(20);
        JTextField descriptionField = new JTextField(20);
        JButton sendButton = new JButton("Send");

        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String actorName = actorNameField.getText();
                String description = descriptionField.getText();
                Actor actor = null;
                for (Actor actor1 : IMDB.getInstance().actors) {
                    if (actor1.name.equals(actorName)) {
                        if((actor1.inserterUsername.equals(user.username)) || (user.userType.equals(AccountType.Admin) && actor1.inserterUsername.equals(AccountType.Admin.toString()))) {
                            JOptionPane.showMessageDialog(actorRequestFrame, "You cannot send a request for an actor you added!", "Error", JOptionPane.ERROR_MESSAGE);
                            break;
                        }
                        actor = actor1;
                        break;
                    }
                }
                if (actor == null) {
                    JOptionPane.showMessageDialog(actorRequestFrame, "Actor not found!", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    Request request = new Request(RequestType.ACTOR_ISSUE, LocalDateTime.now(), null, actorName, description, user.username, actor.inserterUsername);
                    IMDB.getInstance().requests.add(request);
                    request.registerObserver(user);
                    boolean found1 = false;
                    for(User user1 : IMDB.getInstance().users) {
                        if(user1.username.equals(actor.inserterUsername)) {
                            request.registerObserver(user1);
                            request.notifyObservers("You have a new request regarding " + actorName + " from " + request.requesterUsername + "!", 2);
                            found1 = true;
                            JOptionPane.showMessageDialog(actorRequestFrame, "Request sent successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                            break;
                        }
                    }
                    if(!found1) {
                        for(User user1 : IMDB.getInstance().users) {
                            if(user1.userType.equals(AccountType.Admin)) {
                                request.registerObserver(user1);
                                request.notifyObservers("You have a new request regarding " + actorName + "from " + request.requesterUsername + "!", 2);
                                break;
                            }
                        }
                        JOptionPane.showMessageDialog(actorRequestFrame, "Request sent successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        });

        actorRequestFrame.add(new JLabel("Actor Name:"));
        actorRequestFrame.add(actorNameField);
        actorRequestFrame.add(new JLabel("Description:"));
        actorRequestFrame.add(descriptionField);
        actorRequestFrame.add(sendButton);

        actorRequestFrame.setSize(400, 200);
        actorRequestFrame.setLocationRelativeTo(null);
        actorRequestFrame.setVisible(true);
    }

    private void deleteRequestWindow() {
        JFrame deleteRequestFrame = new JFrame("Delete Request");
        deleteRequestFrame.setLayout(new GridLayout(2, 1));

        JTextField requestIdField = new JTextField(20);
        JButton sendButton = new JButton("Send");

        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String requestIdText = requestIdField.getText();
                int requestId;
                try {
                    requestId = Integer.parseInt(requestIdText);
                    int i = 0;
                    for(Request request : IMDB.getInstance().requests) {
                        if(i== requestId) {
                            IMDB.getInstance().requests.remove(i);
                            JOptionPane.showMessageDialog(deleteRequestFrame, "Request deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                            break;
                        }
                        i++;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(deleteRequestFrame, "Please enter a valid request ID!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        deleteRequestFrame.add(new JLabel("Request ID:"));
        deleteRequestFrame.add(requestIdField);
        deleteRequestFrame.add(sendButton);

        deleteRequestFrame.setSize(400, 150);
        deleteRequestFrame.setLocationRelativeTo(null);
        deleteRequestFrame.setVisible(true);
    }

    private void createProductionRequest(User user) {
        JFrame productionRequestFrame = new JFrame("Create Production Request");
        productionRequestFrame.setLayout(new GridLayout(4, 1));

        JTextField productionNameField = new JTextField(20);
        JTextField descriptionField = new JTextField(20);
        JButton sendButton = new JButton("Send");

        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String productionName = productionNameField.getText();
                String description = descriptionField.getText();
                Production production = null;
                for (Production production1 : IMDB.getInstance().productions) {
                    if (production1.productionTitle.equals(productionName)) {
                        if((production1.inserterUsername.equals(user.username)) || (user.userType.equals(AccountType.Admin) && production1.inserterUsername.equals(AccountType.Admin.toString()))) {
                            JOptionPane.showMessageDialog(productionRequestFrame, "You cannot send a request for a production you added!", "Error", JOptionPane.ERROR_MESSAGE);
                            break;
                        }
                        production = production1;
                        break;
                    }
                }
                if (production == null) {
                    JOptionPane.showMessageDialog(productionRequestFrame, "Production not found!", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    Request request = new Request(RequestType.MOVIE_ISSUE, LocalDateTime.now(), productionName, null, description, user.username, production.inserterUsername);
                    IMDB.getInstance().requests.add(request);
                    request.registerObserver(user);
                    for(User user1 : IMDB.getInstance().users) {
                        if(user1.username.equals(production.inserterUsername)) {
                            request.registerObserver(user1);
                            request.notifyObservers("You have a new request regarding " + productionName + "from " + request.requesterUsername + "!", 2);
                            JOptionPane.showMessageDialog(productionRequestFrame, "Request sent successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                            break;
                        }
                    }
                }
            }
        });

        productionRequestFrame.add(new JLabel("Production Name:"));
        productionRequestFrame.add(productionNameField);
        productionRequestFrame.add(new JLabel("Description:"));
        productionRequestFrame.add(descriptionField);
        productionRequestFrame.add(sendButton);

        productionRequestFrame.setSize(400, 200);
        productionRequestFrame.setLocationRelativeTo(null);
        productionRequestFrame.setVisible(true);
    }

    private void deleteAccountRequest(User user) {
        JFrame deleteAccountFrame = new JFrame("Delete Account Request");
        deleteAccountFrame.setLayout(new GridLayout(3, 1));

        JTextField descriptionField = new JTextField(20);
        JButton sendButton = new JButton("Send");

        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String description = descriptionField.getText();
                Request request = new Request(RequestType.DELETE_ACCOUNT, LocalDateTime.now(), null, null, description, user.username, "Admin");
                RequestsHolderMain.RequestsHolder.addRequest(request);
                IMDB.getInstance().moveRequests();
                request.registerObserver(user);
                for(User user1 : IMDB.getInstance().users) {
                    if(user1.userType.equals(AccountType.Admin)) {
                        request.registerObserver(user1);
                        request.notifyObservers("You have a new delete request from " + user.username + "!", 2);
                    }
                }
                JOptionPane.showMessageDialog(deleteAccountFrame, "Request sent successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        deleteAccountFrame.add(new JLabel("Description:"));
        deleteAccountFrame.add(descriptionField);
        deleteAccountFrame.add(sendButton);

        deleteAccountFrame.setSize(400, 150);
        deleteAccountFrame.setLocationRelativeTo(null);
        deleteAccountFrame.setVisible(true);
    }

    private void otherRequest(User user) {
        JFrame otherRequestFrame = new JFrame("Other Request");
        otherRequestFrame.setLayout(new GridLayout(3, 1));

        JTextField descriptionField = new JTextField(20);
        JButton sendButton = new JButton("Send");

        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String description = descriptionField.getText();
                Request request = new Request(RequestType.OTHERS, LocalDateTime.now(), null, null, description, user.username, "Admin");
                RequestsHolderMain.RequestsHolder.addRequest(request);
                IMDB.getInstance().moveRequests();
                request.registerObserver(user);
                for(User user1 : IMDB.getInstance().users) {
                    if(user1.userType.equals(AccountType.Admin)) {
                        request.registerObserver(user1);
                        request.notifyObservers("You have a new request from " + user.username + "!", 2);
                    }
                }
                JOptionPane.showMessageDialog(otherRequestFrame, "Request sent successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        otherRequestFrame.add(new JLabel("Description:"));
        otherRequestFrame.add(descriptionField);
        otherRequestFrame.add(sendButton);

        otherRequestFrame.setSize(400, 150);
        otherRequestFrame.setLocationRelativeTo(null);
        otherRequestFrame.setVisible(true);
    }

    private void viewRequestsWindow() {
        JFrame viewRequestsFrame = new JFrame("View Requests");
        viewRequestsFrame.setLayout(new BorderLayout());

        JTextArea requestsTextArea = new JTextArea(20, 40);
        requestsTextArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(requestsTextArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        int i = 0;
        for (Request request : IMDB.getInstance().requests) {
            requestsTextArea.append("Request ID: " + i + "\n");
            if(request.actorName != null) {
                requestsTextArea.append("\tActor Name: " + request.actorName + "\n");
            } else if(request.productionTitle != null) {
                requestsTextArea.append("\tProduction Name: " + request.productionTitle + "\n");
            }
            requestsTextArea.append("\tRequest Description: " + request.problemDescription + "\n");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            String formatDateTime = request.getRequestDate().format(formatter);
            requestsTextArea.append("\tRequest Date: " + formatDateTime + "\n");
            i++;
        }

        viewRequestsFrame.add(scrollPane, BorderLayout.CENTER);

        viewRequestsFrame.setSize(500, 400);
        viewRequestsFrame.setLocationRelativeTo(null);
        viewRequestsFrame.setVisible(true);
    }

    public void displayFavorites(User user) {
        JFrame favoritesFrame = new JFrame();
        favoritesFrame.setTitle("Favorites");
        favoritesFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        favoritesFrame.setSize(400, 300);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 1));

        JButton addActorButton = new JButton("Add Actor to Favorites");
        addActorButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String actorName = JOptionPane.showInputDialog(mainScreenFrame, "Enter actor name:");
                if (!actorName.isEmpty()) {
                    Actor actorToAdd = null;
                    for (Actor actor : IMDB.getInstance().actors) {
                        if (actor.name.equals(actorName)) {
                            actorToAdd = actor;
                            break;
                        }
                    }
                    if (actorToAdd != null) {
                        if (user.isActorInPreferences(new Actor(actorName, null, null, null))) {
                            JOptionPane.showMessageDialog(favoritesFrame, "Actor already in favorites!", "Error", JOptionPane.ERROR_MESSAGE);
                        } else {
                            user.addPreference(actorToAdd);
                        }
                    } else if (actorToAdd == null) {
                        JOptionPane.showMessageDialog(favoritesFrame, "Actor not found!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(favoritesFrame, "Please enter actor's name correctly!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        panel.add(addActorButton);

        JButton addProductionButton = new JButton("Add Production to Favorites");
        addProductionButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String productionName = JOptionPane.showInputDialog(mainScreenFrame, "Enter production name:");
                if (!productionName.isEmpty()) {
                    Production productionToAdd = null;
                    for (Production production : IMDB.getInstance().productions) {
                        if (production.productionTitle.equals(productionName)) {
                            productionToAdd = production;
                            break;
                        }
                    }
                    if (productionToAdd != null) {
                        if (user.isProductionInPreferences(productionToAdd)) {
                            JOptionPane.showMessageDialog(favoritesFrame, "Production already in favorites!", "Error", JOptionPane.ERROR_MESSAGE);
                        } else {
                            user.addPreference(productionToAdd);
                        }
                    } else if (productionToAdd == null) {
                        JOptionPane.showMessageDialog(favoritesFrame, "Production not found!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(favoritesFrame, "Please enter production's name!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        panel.add(addProductionButton);

        JButton deleteActorButton = new JButton("Delete Actor from Favorites");
        deleteActorButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String actor = JOptionPane.showInputDialog(mainScreenFrame, "Enter actor name:");
                if (!actor.isEmpty()) {
                    Actor actorToDelete = null;
                    for (Actor a : IMDB.getInstance().actors) {
                        if (a.name.equals(actor)) {
                            actorToDelete = a;
                            break;
                        }
                    }
                    if (actorToDelete != null) {
                        if (user.isActorInPreferences(actorToDelete)) {
                            user.removePreference(actorToDelete);
                        } else {
                            JOptionPane.showMessageDialog(favoritesFrame, "Actor not in favorites!", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else if (actorToDelete == null) {
                        JOptionPane.showMessageDialog(favoritesFrame, "Actor not found!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(favoritesFrame, "Please enter actor's name correctly!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        panel.add(deleteActorButton);

        JButton deleteProductionButton = new JButton("Delete Production from Favorites");
        deleteProductionButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String production = JOptionPane.showInputDialog(mainScreenFrame, "Enter production name:");
                if (!production.isEmpty()) {
                    Production productionToDelete = null;
                    for (Production p : IMDB.getInstance().productions) {
                        if (p.productionTitle.equals(production)) {
                            productionToDelete = p;
                            break;
                        }
                    }
                    if (productionToDelete != null) {
                        if (user.isProductionInPreferences(productionToDelete)) {
                            user.removePreference(productionToDelete);
                        } else {
                            JOptionPane.showMessageDialog(favoritesFrame, "Production not in favorites!", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else if (productionToDelete == null) {
                        JOptionPane.showMessageDialog(favoritesFrame, "Production not found!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(favoritesFrame, "Please enter production's name!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        panel.add(deleteProductionButton);

        JButton showFavoritesButton = new JButton("Show Favorites");
        showFavoritesButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayFavoritesList(user);
            }
        });
        panel.add(showFavoritesButton);

        JButton backButton = new JButton("Back");
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.setForeground(Color.WHITE);
        backButton.setBackground(new Color(50, 205, 50));
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                favoritesFrame.dispose();
                displayChoices(user);
            }
        });
        panel.add(backButton);

        favoritesFrame.add(panel);
        favoritesFrame.setLocationRelativeTo(null);
        favoritesFrame.setVisible(true);
    }

    private void displayFavoritesList(User user) {
        JFrame favoritesListFrame = new JFrame();
        favoritesListFrame.setTitle("Favorites List");
        favoritesListFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        favoritesListFrame.setSize(400, 300);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        for (Object favorite : user.userPreferences) {
            JLabel nameLabel = new JLabel();
            if (favorite instanceof Actor) {
                nameLabel.setText(((Actor) favorite).name);
            } else if (favorite instanceof Production) {
                nameLabel.setText(((Production) favorite).productionTitle);
            }
            panel.add(nameLabel);
        }

        JScrollPane scrollPane = new JScrollPane(panel);
        favoritesListFrame.add(scrollPane);
        favoritesListFrame.setLocationRelativeTo(null);
        favoritesListFrame.setVisible(true);
    }

    public void displayActorDetails(User user, Actor actor, JPanel contentPanel) {
        JPanel actorPanel = new JPanel();
        actorPanel.setLayout(new GridLayout(1, 2));

        JPanel actorDetailsPanel = new JPanel();
        actorDetailsPanel.setLayout(new BoxLayout(actorDetailsPanel, BoxLayout.Y_AXIS));

        addLabelAndTextArea(actorDetailsPanel, "Actor name:", actor.name);
        addLabelAndTextArea(actorDetailsPanel, "Actor biography:", actor.biography);
        addLabelAndTextArea(actorDetailsPanel, "Actor pair list:", "");

        if (actor.pairList == null) {
            addLabelAndTextArea(actorDetailsPanel, "\tProduction Type:", "None");
            addLabelAndTextArea(actorDetailsPanel, "\tProduction Name:", "None");
        } else {
            for (Pair<String, ProductionType> pair : actor.pairList) {
                addLabelAndTextArea(actorDetailsPanel, "\tProduction Type:", pair.productionType.toString());
                addLabelAndTextArea(actorDetailsPanel, "\tProduction Name:", pair.name);
            }
        }

        actorPanel.add(actorDetailsPanel);

        JPanel actorImagePanel = new JPanel();
        String path = "C:\\Users\\sandu\\IdeaProjects\\Tema1_POO_IMDB\\assets\\actors\\" + actor.name + ".jpeg";
        ImageIcon actorImage = new ImageIcon(path);
        JLabel actorImageLabel = new JLabel(actorImage);
        actorImagePanel.add(actorImageLabel);
        actorPanel.add(actorImagePanel);

        contentPanel.add(actorPanel);
        contentPanel.add(actorPanel);
        contentPanel.setSize(400, 400);
        contentPanel.setVisible(true);
    }


    public void displaySearch(User user) {
        JFrame mainScreenFrame = new JFrame();
        mainScreenFrame.setTitle("Search Actor/Movie/Series");
        mainScreenFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainScreenFrame.setSize(400, 400);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(135, 206, 250));

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JLabel label = new JLabel("Search Actor/Movie/Series");
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(label);


        JButton searchActorButton = new JButton("Search Actor");
        searchActorButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        searchActorButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String actor = JOptionPane.showInputDialog(mainScreenFrame, "Enter actor name:");
                boolean actorFound = false;
                if (actor != null && !actor.isEmpty()) {
                    for(Actor a : IMDB.getInstance().actors) {
                        if(a.name.equals(actor)) {
                            mainScreenFrame.dispose();
                            displayActorDetailsSearch(user, a);
                            actorFound = true;
                            break;
                        }
                    }
                    if(!actorFound) {
                        JOptionPane.showMessageDialog(null, "Actor not found!", "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        JButton backButton = new JButton("Back");
                        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
                        backButton.setForeground(Color.WHITE);
                        backButton.setBackground(new Color(50, 205, 50));
                        backButton.setFont(new Font("Arial", Font.BOLD, 14));
                        backButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
                        backButton.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                                mainScreenFrame.dispose();
                                displayChoices(user);
                            }
                        });
                        panel.add(backButton);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Please enter a valid actor name!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        panel.add(searchActorButton);

        JButton searchMovieButton = new JButton("Search Movie");
        searchMovieButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        searchMovieButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String movie = JOptionPane.showInputDialog(mainScreenFrame, "Enter movie name:");
                if (movie != null && !movie.isEmpty()) {
                    boolean movieFound = false;
                    for(Production m : IMDB.getInstance().productions) {
                        if(m instanceof Movie && ((Movie)m).movieName.equals(movie)) {
                            mainScreenFrame.dispose();
                            displayMovieDetails(panel, (Movie)m);
                            movieFound = true;
                            break;
                        }
                    }
                    if(!movieFound) {
                        JOptionPane.showMessageDialog(null, "Movie not found!", "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        JButton backButton = new JButton("Back");
                        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
                        backButton.setForeground(Color.WHITE);
                        backButton.setBackground(new Color(50, 205, 50));
                        backButton.setFont(new Font("Arial", Font.BOLD, 14));
                        backButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
                        backButton.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                                mainScreenFrame.dispose();
                                displayChoices(user);
                            }
                        });
                        panel.add(backButton);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Please enter a valid movie name!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        panel.add(searchMovieButton);

        JButton displaySeriesButton = new JButton("Search Series");
        displaySeriesButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        displaySeriesButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String series = JOptionPane.showInputDialog(mainScreenFrame, "Enter series name:");
                if (series != null && !series.isEmpty()) {
                    boolean seriesFound = false;
                    for(Production s : IMDB.getInstance().productions) {
                        if(s instanceof Series && ((Series)s).productionTitle.equals(series)) {
                            mainScreenFrame.dispose();
                            displaySeriesDetails(panel, (Series)s);
                            seriesFound = true;
                            break;
                        }
                    }
                    if(!seriesFound) {
                        JOptionPane.showMessageDialog(null, "Series not found!", "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        JButton backButton = new JButton("Back");
                        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
                        backButton.setForeground(Color.WHITE);
                        backButton.setBackground(new Color(50, 205, 50));
                        backButton.setFont(new Font("Arial", Font.BOLD, 14));
                        backButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
                        backButton.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                                mainScreenFrame.dispose();
                                displayChoices(user);
                            }
                        });
                        panel.add(backButton);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Please enter a valid series name!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        panel.add(displaySeriesButton);

        JButton backButton = new JButton("Back");
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.setForeground(Color.WHITE);
        backButton.setBackground(new Color(50, 205, 50));
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mainScreenFrame.dispose();
                displayChoices(user);
            }
        });
        panel.add(backButton);

        mainScreenFrame.add(scrollPane);
        mainScreenFrame.setLocationRelativeTo(null);
        mainScreenFrame.setVisible(true);
    }

    public void displayNotifications(User user) {
        JFrame mainScreenFrame = new JFrame();
        mainScreenFrame.setTitle("Notifications");
        mainScreenFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainScreenFrame.setSize(400, 400);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(135, 206, 250));

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JLabel label = new JLabel("Notifications");
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(label);

        for (Object notification : user.userNotifications) {
            JLabel notificationLabel = new JLabel(String.valueOf(notification));
            notificationLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            notificationLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            panel.add(notificationLabel);
        }

        JButton backButton = new JButton("Back");
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mainScreenFrame.dispose();
                displayChoices(user);
            }
        });
        panel.add(backButton);

        mainScreenFrame.add(scrollPane);
        mainScreenFrame.setLocationRelativeTo(null);
        mainScreenFrame.setVisible(true);
    }

    public void displayActorsDetails(User user, ArrayList<Actor> actors) {
        JFrame mainScreenFrame = new JFrame();
        mainScreenFrame.setTitle("Actors Details");
        mainScreenFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainScreenFrame.setSize(400, 400);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(135, 206, 250));

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JLabel label = new JLabel("Actors Details");
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(label);

        JButton sortedByName = new JButton("Sort by name");
        sortedByName.setAlignmentX(Component.CENTER_ALIGNMENT);
        sortedByName.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ArrayList<Actor> newActors = new ArrayList<>(actors);
                Collections.sort(newActors);
                mainScreenFrame.dispose();
                showActorsDetails(user, newActors, scrollPane);
            }
        });
        panel.add(sortedByName);

        JButton searchDefault = new JButton("Display all actors");
        searchDefault.setAlignmentX(Component.CENTER_ALIGNMENT);
        searchDefault.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mainScreenFrame.dispose();
                showActorsDetails(user, actors, scrollPane);
            }
        });
        panel.add(searchDefault);

        JButton backButton = new JButton("Back");
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mainScreenFrame.dispose();
                displayChoices(user);
            }
        });
        panel.add(backButton);

        mainScreenFrame.add(scrollPane);
        mainScreenFrame.setLocationRelativeTo(null);
        mainScreenFrame.setVisible(true);
    }


    public void displayProductionsDetails(User user) {
        JFrame mainScreenFrame = new JFrame();
        mainScreenFrame.setTitle("Productions Details");
        mainScreenFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainScreenFrame.setSize(400, 400);
        mainScreenFrame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(135, 206, 250));

        JLabel label = new JLabel("Productions Details");
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(label);

        JButton searchByGenreButton = new JButton("Search by Genre");
        searchByGenreButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        searchByGenreButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String genre = JOptionPane.showInputDialog(mainScreenFrame, "Enter genre:");
                boolean genreFound = false;
                if (genre != null && !genre.isEmpty()) {
                    for(Genre g : Genre.values()) {
                        if(g.name().equals(genre)) {
                            displayProductionsByGenre(user, genre);
                            genreFound = true;
                            break;
                        }
                    }
                    if(!genreFound) {
                        JOptionPane.showMessageDialog(null, "Genre not found!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Please enter a genre!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        panel.add(searchByGenreButton);

        JButton searchByReviewsButton = new JButton("Search by Number of Reviews");
        searchByReviewsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        searchByReviewsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String reviewsInput = JOptionPane.showInputDialog(mainScreenFrame, "Enter number of reviews:");
                if (reviewsInput != null && !reviewsInput.isEmpty()) {
                    try {
                        int numberOfReviews = Integer.parseInt(reviewsInput);
                        mainScreenFrame.dispose();
                        displayProductionsByReviews(user, numberOfReviews);
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "Enter a valid number of reviews", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Enter a valid number of reviews", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        panel.add(searchByReviewsButton);

        JButton displayAllProductionsButton = new JButton("Display All Productions");
        displayAllProductionsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        displayAllProductionsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mainScreenFrame.dispose();
                displayAllProductions(user);
            }
        });
        panel.add(displayAllProductionsButton);

        JButton backButton = new JButton("Back");
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.setForeground(Color.WHITE);
        backButton.setBackground(new Color(50, 205, 50));
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mainScreenFrame.dispose();
                displayChoices(user);
            }
        });
        panel.add(backButton);

        mainScreenFrame.add(panel);
        mainScreenFrame.setLocationRelativeTo(null);
        mainScreenFrame.setVisible(true);
    }

    public void displayProductionsByGenre(User user, String genre) {
        JFrame mainScreenFrame = new JFrame();
        mainScreenFrame.setTitle("Productions Details");
        mainScreenFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mainScreenFrame.setSize(400, 400);
        mainScreenFrame.setLocationRelativeTo(null);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());

        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(detailsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        contentPanel.add(scrollPane, BorderLayout.CENTER);

        for (Production production : IMDB.getInstance().productions) {
            if (production.genres.contains(Genre.valueOf(genre))) {
                JPanel panel = new JPanel();
                panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

                if (production instanceof Movie) {
                    Movie movie = (Movie) production;
                    displayMovieDetails(detailsPanel, movie);
                } else if (production instanceof Series) {
                    Series series = (Series) production;
                    displaySeriesDetails(detailsPanel, series);
                }

            }
        }

        mainScreenFrame.add(contentPanel, BorderLayout.CENTER);

        JPanel backButtonPanel = new JPanel();
        JButton backButton = new JButton("Back");
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.setForeground(Color.WHITE);
        backButton.setBackground(new Color(50, 205, 50));
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mainScreenFrame.dispose();
                displayChoices(user);
            }
        });
        backButtonPanel.add(backButton);

        mainScreenFrame.add(backButtonPanel, BorderLayout.SOUTH);

        mainScreenFrame.setVisible(true);
    }

    private void addLabelAndTextArea(JPanel panel, String labelText, String text) {
        JPanel subPanel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(labelText);
        JTextArea textArea = new JTextArea(text);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        subPanel.add(label, BorderLayout.NORTH);
        subPanel.add(new JScrollPane(textArea), BorderLayout.CENTER);
        panel.add(subPanel);
    }

    private String formatGenres(ArrayList<Genre> genres) {
        StringBuilder formattedGenres = new StringBuilder();
        for (Genre genre : genres) {
            formattedGenres.append(genre.name()).append("\n");
        }
        return formattedGenres.toString();
    }

    private String formatRatings(ArrayList<Rating> ratings) {
        StringBuilder formattedRatings = new StringBuilder();
        for (Rating rating : ratings) {
            formattedRatings.append("Rating: ").append(rating.rating).append("\n");
            formattedRatings.append("Comments: ").append(rating.comments).append("\n");
            formattedRatings.append("User: ").append(rating.user).append("\n\n");
        }
        return formattedRatings.toString();
    }

    private String formatComments(ArrayList<String> comments) {
        StringBuilder formattedComments = new StringBuilder();
        for (String comment : comments) {
            formattedComments.append(comment).append("\n");
        }
        return formattedComments.toString();
    }

    private void configureTextArea(JTextArea textArea, int width, int height) {
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setPreferredSize(new Dimension(width, height));
    }

    public void displayProductionsByReviews(User user, int numberOfReviews) {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JFrame mainScreenFrame = new JFrame();
        mainScreenFrame.setTitle("Productions Details");
        mainScreenFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mainScreenFrame.setSize(400, 400);

        for (Production production : IMDB.getInstance().productions) {
            if (production.ratings.size() == numberOfReviews) {
                JPanel panel = new JPanel();
                panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

                if (production instanceof Movie) {
                    Movie movie = (Movie) production;
                    displayMovieDetails(panel, movie);

                } else if (production instanceof Series) {
                    Series series = (Series) production;
                    displaySeriesDetails(panel, series);
                }

                contentPanel.add(panel);
            }
        }

        JButton backButton = new JButton("Back");
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.setForeground(Color.WHITE);
        backButton.setBackground(new Color(50, 205, 50));
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mainScreenFrame.dispose();
                displayChoices(user);
            }
        });
        contentPanel.add(backButton);

        mainScreenFrame.add(scrollPane);
        mainScreenFrame.setLocationRelativeTo(null);
        mainScreenFrame.setVisible(true);
    }

    public void displayAllProductions(User user) {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JFrame mainScreenFrame = new JFrame();
        mainScreenFrame.setTitle("Productions Details");
        mainScreenFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mainScreenFrame.setSize(400, 400);

        for (Production production : IMDB.getInstance().productions) {
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

            if (production instanceof Movie) {
                Movie movie = (Movie) production;
                displayMovieDetails(panel, movie);
            } else if (production instanceof Series) {
                Series series = (Series) production;
                displaySeriesDetails(panel, series);
            }

            contentPanel.add(panel);
        }

        JButton backButton = new JButton("Back");
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.setForeground(Color.WHITE);
        backButton.setBackground(new Color(50, 205, 50));
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mainScreenFrame.dispose();
                displayChoices(user);
            }
        });
        contentPanel.add(backButton);

        mainScreenFrame.add(scrollPane);
        mainScreenFrame.setLocationRelativeTo(null);
        mainScreenFrame.setVisible(true);
    }

    public void showActorsDetails(User user, ArrayList<Actor> actors, JScrollPane scrollPane) {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        JFrame mainScreenFrame = new JFrame();
        mainScreenFrame.setTitle("Actors Details");
        mainScreenFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainScreenFrame.setSize(400, 400);
        mainScreenFrame.getContentPane().setBackground(new Color(135, 206, 250));

        for (Actor actor : actors) {
            displayActorDetails(user, actor, contentPanel);
        }

        JButton backButton = new JButton("Back");
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.setForeground(Color.WHITE);
        backButton.setBackground(new Color(50, 205, 50));
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mainScreenFrame.dispose();
                displayChoices(user);
            }
        });
        contentPanel.add(backButton);
        scrollPane.setViewportView(contentPanel);
        mainScreenFrame.add(scrollPane);
        mainScreenFrame.setLocationRelativeTo(null);
        mainScreenFrame.setVisible(true);
    }

    public void displayMovieDetails(JPanel panel, Movie movie) {
        JPanel moviePanel = new JPanel();
        moviePanel.setLayout(new GridLayout(1, 2));

        JPanel movieDetailsPanel = new JPanel();
        movieDetailsPanel.setLayout(new BoxLayout(movieDetailsPanel, BoxLayout.Y_AXIS));

        addLabelAndTextArea(movieDetailsPanel, "Movie Name:", movie.movieName);
        addLabelAndTextArea(movieDetailsPanel, "Description:", movie.movieDescription);
        addLabelAndTextArea(movieDetailsPanel, "Duration:", movie.movieDuration);
        addLabelAndTextArea(movieDetailsPanel, "Release Year:", String.valueOf(movie.releaseYear));
        addLabelAndTextArea(movieDetailsPanel, "Directors:", String.join(", ", movie.directors));
        addLabelAndTextArea(movieDetailsPanel, "Actors:", String.join(", ", movie.actors));
        addLabelAndTextArea(movieDetailsPanel, "Genres:", formatGenres((ArrayList<Genre>) movie.genres));
        addLabelAndTextArea(movieDetailsPanel, "Ratings:", formatRatings((ArrayList<Rating>) movie.ratings));
        addLabelAndTextArea(movieDetailsPanel, "Comments:", formatComments((ArrayList<String>) movie.comments));
        addLabelAndTextArea(movieDetailsPanel, "Final Rating:", String.valueOf(movie.finalRating));

        moviePanel.add(movieDetailsPanel);

        JPanel movieImagePanel = new JPanel();
        movieImagePanel.setBackground(Color.LIGHT_GRAY);
        movieImagePanel.setPreferredSize(new Dimension(300, 300));
        moviePanel.add(movieImagePanel);

        panel.add(moviePanel);
        add(moviePanel);
        setSize(400, 400);
        setVisible(true);
    }

    public void displaySeriesDetails(JPanel panel, Series series) {
        JPanel seriesPanel = new JPanel();
        seriesPanel.setLayout(new GridLayout(1, 2));

        JPanel seriesDetailsPanel = new JPanel();
        seriesDetailsPanel.setLayout(new BoxLayout(seriesDetailsPanel, BoxLayout.Y_AXIS));

        addLabelAndTextArea(seriesDetailsPanel, "Series Name:", series.productionTitle);
        addLabelAndTextArea(seriesDetailsPanel, "Description:", series.movieDescription);
        addLabelAndTextArea(seriesDetailsPanel, "Release Year:", String.valueOf(series.releaseYear));
        addLabelAndTextArea(seriesDetailsPanel, "Directors:", String.join(", ", series.directors));
        addLabelAndTextArea(seriesDetailsPanel, "Actors:", String.join(", ", series.actors));
        addLabelAndTextArea(seriesDetailsPanel, "Genres:", formatGenres((ArrayList<Genre>) series.genres));
        addLabelAndTextArea(seriesDetailsPanel, "Ratings:", formatRatings((ArrayList<Rating>) series.ratings));
        addLabelAndTextArea(seriesDetailsPanel, "Comments:", formatComments((ArrayList<String>) series.comments));
        addLabelAndTextArea(seriesDetailsPanel, "Final Rating:", String.valueOf(series.finalRating));

        seriesPanel.add(seriesDetailsPanel);

        JPanel seriesImagePanel = new JPanel();
        seriesImagePanel.setBackground(Color.LIGHT_GRAY);
        seriesImagePanel.setPreferredSize(new Dimension(300, 300));
        seriesPanel.add(seriesImagePanel);

        panel.add(seriesPanel);
        add(seriesPanel);
        setSize(400, 400);
        setVisible(true);
    }


    private void showMessage(String message, Color color) {
        JOptionPane.showMessageDialog(loginFrame, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void run() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                displayLoginPage();
            }
        });
    }
}
