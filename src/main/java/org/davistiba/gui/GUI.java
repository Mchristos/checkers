package org.davistiba.gui;

import org.davistiba.game.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class GUI extends JFrame {

    private Game game;
    private ArrayList<BoardState> possibleMoves;
    private SquarePanel[] squares;
    private JPanel checkerboardPanel;
    private JTextArea textBox;
    // hint feature
    private BoardState hintMove;
    private List<Integer> helpMoves;
    private final HashMap<Integer, Integer> difficultyMapping;
    private final String rulesList = getRulesList();
    private final ScheduledExecutorService executor;
    private final Font MY_HELVETICA;

    private static final Logger logger = Logger.getLogger(GUI.class.getName());

    public GUI() {
        this.setTitle("Checkers");
        difficultyMapping = new HashMap<>();
        difficultyMapping.put(1, 1);
        difficultyMapping.put(2, 5);
        difficultyMapping.put(3, 8);
        difficultyMapping.put(4, 12);
        this.executor = Executors.newSingleThreadScheduledExecutor();
        MY_HELVETICA = new Font(Font.DIALOG, Font.PLAIN, 12);
        start();
    }

    private void start() {
        settingsPopup();
        game = new Game();
        possibleMoves = new ArrayList<>();
        hintMove = null;
        setup();
        if (SettingsPanel.hintMode) {
            onHintClick();
        }
    }

    /**
     * Pop up dialog for user to choose game settings (e.g. AI difficulty, starting player etc)
     */
    private void settingsPopup() {
        // panel for options
        JPanel panel = new JPanel(new GridLayout(5, 1));
        // difficulty slider
        JLabel text1 = new JLabel("Set Difficulty", SwingConstants.LEADING);
        JSlider slider = new JSlider();
        slider.setSnapToTicks(true);
        slider.setMaximum(4);
        slider.setMinimum(1);
        slider.setMajorTickSpacing(1);

        //construct labels
        Hashtable<Integer, JLabel> labels = new Hashtable<>();
        labels.put(1, new JLabel("Easy"));
        labels.put(2, new JLabel("Medium"));
        labels.put(3, new JLabel("Hard"));
        labels.put(4, new JLabel("On Fire"));
        labels.forEach((k, v) -> v.setFont(MY_HELVETICA));

        //construct sliders
        slider.setLabelTable(labels);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setPreferredSize(new Dimension(200, 50));
        slider.setValue(3);
        // force takes option
        JRadioButton forceTakesButton = new JRadioButton("Force Takes");
        forceTakesButton.setSelected(Settings.FORCETAKES);
        // who gets first move?
        ButtonGroup buttonGroup = new ButtonGroup();
        JRadioButton humanFirstRadioButton = new JRadioButton("You Play First");
        JRadioButton aiRadioButton = new JRadioButton("Computer Plays First");
        buttonGroup.add(humanFirstRadioButton);
        buttonGroup.add(aiRadioButton);
        aiRadioButton.setSelected(Settings.FIRSTMOVE == Player.AI);
        humanFirstRadioButton.setSelected(Settings.FIRSTMOVE == Player.HUMAN);
        // add components to panel
        panel.add(text1);
        panel.add(slider);
        panel.add(forceTakesButton);
        panel.add(humanFirstRadioButton);
        panel.add(aiRadioButton);
        // pop up
        int result = JOptionPane.showConfirmDialog(null, panel, "Game Settings",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        // process results
        if (result == JOptionPane.OK_OPTION) {
            Settings.AI_DEPTH = difficultyMapping.get(slider.getValue());
            logger.info("Selected AI depth = " + Settings.AI_DEPTH);
            Settings.FIRSTMOVE = humanFirstRadioButton.isSelected() ? Player.HUMAN : Player.AI;
            Settings.FORCETAKES = forceTakesButton.isSelected();
        } else {
            this.dispose();
            System.exit(0);
        }
    }


    /**
     * Sets up initial GUI configuration.
     */
    public void setup() {
        switch (Settings.FIRSTMOVE) {
            case AI:
                SettingsPanel.AIcolour = Colour.WHITE;
                break;
            case HUMAN:
                SettingsPanel.AIcolour = Colour.BLACK;
                break;
        }

        setupMenuBar();
        JPanel contentPane = new JPanel();
        checkerboardPanel = new JPanel(new GridBagLayout());
        JPanel textPanel = new JPanel();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));
        this.setContentPane(contentPane);
        contentPane.add(checkerboardPanel);
        contentPane.add(textPanel);
        textBox = new JTextArea();
        textBox.setFont(MY_HELVETICA);
        textBox.setForeground(Color.RED);
        textBox.setEditable(false);
        textBox.setLineWrap(false);
        textBox.setCaretColor(Color.RED);
        textBox.setWrapStyleWord(true);
        textBox.setAutoscrolls(true);
        textPanel.add(textBox);

        updateCheckerBoard();
        updateText("");
        this.pack();
        this.setVisible(true);
        this.setResizable(false);
        if (Settings.FIRSTMOVE == Player.AI) {
            aiMove();
        }
    }

    private void updateText(String text) {
        textBox.setText(text);
    }

    /**
     * Updates the checkerboard GUI based on the game state.
     */
    private void updateCheckerBoard() {
        checkerboardPanel.removeAll();
        addPieces();
        addSquares();
        addGhostButtons();
        checkerboardPanel.setVisible(true);
        checkerboardPanel.repaint();
        this.pack();
        this.setVisible(true);
    }

    private void addSquares() {
        squares = new SquarePanel[BoardState.NUM_SQUARES];
        int fromPos = -1;
        int toPos = -1;
        if (hintMove != null) {
            fromPos = hintMove.getFromPos();
            toPos = hintMove.getToPos();
        }
        GridBagConstraints c = new GridBagConstraints();
        for (int i = 0; i < BoardState.NUM_SQUARES; i++) {
            c.gridx = i % BoardState.SIDE_LENGTH;
            c.gridy = i / BoardState.SIDE_LENGTH;
            squares[i] = new SquarePanel(c.gridx, c.gridy);
            if (i == fromPos) {
                squares[i].setHighlighted();
            }
            if (i == toPos) {
                squares[i].setHighlighted();
            }
            if (helpMoves != null) {
                if (helpMoves.contains(i)) {
                    squares[i].setHighlighted();
                }
            }
            checkerboardPanel.add(squares[i], c);
        }
    }


    /**
     * Add checker pieces to the GUI corresponding to the game state
     */
    private void addPieces() {
        GridBagConstraints c = new GridBagConstraints();
        game.getState();
        for (int i = 0; i < BoardState.NUM_SQUARES; i++) {
            game.getState();
            c.gridx = i % BoardState.SIDE_LENGTH;
            game.getState();
            c.gridy = i / BoardState.SIDE_LENGTH;
            if (game.getState().getPiece(i) != null) {
                Piece piece = game.getState().getPiece(i);
                CheckerButton button = new CheckerButton(i, piece, this);
                button.addActionListener(this::onPieceClick);
                checkerboardPanel.add(button, c);
            }
        }
    }

    /**
     * Add "ghost buttons" showing possible moves for the player
     */
    private void addGhostButtons() {
        for (BoardState state : possibleMoves) {
            int newPos = state.getToPos();

            GhostButton button = new GhostButton(state);
            button.addActionListener(this::onGhostButtonClick);
            squares[newPos].add(button);
        }
    }


    /**
     * Sets up the menu bar component.
     */
    private void setupMenuBar() {

        // ensure exit method is called on window closing
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
                                   @Override
                                   public void windowClosing(WindowEvent e) {
                                       onExitClick();
                                   }
                               }
        );
        // initialize components
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem restartItem = new JMenuItem("Restart");
        JMenuItem quitItem = new JMenuItem("Quit");
        JMenu editMenu = new JMenu("Edit");
        JMenuItem undoItem = new JMenuItem("Undo");
        JMenu viewMenu = new JMenu("View");
        JRadioButtonMenuItem viewItemHelpMode = new JRadioButtonMenuItem("Help mode");
        JRadioButtonMenuItem viewItemHintMode = new JRadioButtonMenuItem("Hint mode");
        viewItemHelpMode.setSelected(SettingsPanel.helpMode);
        viewItemHintMode.setSelected(SettingsPanel.hintMode);
        JMenu helpMenu = new JMenu("Help");
        JMenuItem rulesItem = new JMenuItem("Game Rules");
        JMenuItem helpItemHint = new JMenuItem("Hint!");
        JMenuItem helpItemMovables = new JMenuItem("Show movable pieces");

        // add action listeners
        quitItem.addActionListener((e) -> onExitClick());
        restartItem.addActionListener(e -> onRestartClick());
        rulesItem.addActionListener(e -> onRulesClick());
        undoItem.addActionListener(e -> onUndoClick());
        viewItemHelpMode.addActionListener(e -> onHelpModeClick());
        viewItemHintMode.addActionListener(e -> onHintModeClick());
        helpItemHint.addActionListener(e -> onHintClick());
        helpItemMovables.addActionListener(e -> onHelpMovablesClick());


        // add components to menu bar
        fileMenu.add(restartItem);
        fileMenu.add(quitItem);
        editMenu.add(undoItem);
        viewMenu.add(viewItemHelpMode);
        viewMenu.add(viewItemHintMode);
        helpMenu.add(helpItemHint);
        helpMenu.add(helpItemMovables);
        helpMenu.add(rulesItem);
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(viewMenu);
        menuBar.add(helpMenu);
        this.setJMenuBar(menuBar);
    }

    //**************************************************************/
    //*********************** ON CLICK METHODS **********************/

    public void onMouseRelease(int position, int dx, int dy) {
        MoveFeedback feedback = game.playerMove(position, dx, dy);
        if (feedback == MoveFeedback.SUCCESS) {
            updateCheckerBoard();
            aiMove();
        } else {
            updateCheckerBoard();
            logger.info(feedback.toString());
        }
    }

    private void onHintClick() {
        if (!game.isGameOver()) {
            AI ai = new AI(10, Player.HUMAN);
            helpMoves = null;
            hintMove = ai.move(this.game.getState(), Player.HUMAN);
            updateCheckerBoard();
        }
    }

    private void onHelpMovablesClick() {
        hintMove = null;
        helpMoves = game.getState().getSuccessors().stream().map(BoardState::getFromPos).collect(Collectors.toList());
        updateCheckerBoard();
    }

    private void onHelpModeClick() {
        SettingsPanel.helpMode = !SettingsPanel.helpMode;
        logger.info("help mode: " + SettingsPanel.helpMode);
    }

    private void onHintModeClick() {
        SettingsPanel.hintMode = !SettingsPanel.hintMode;
        logger.info("hint mode: " + SettingsPanel.hintMode);
        onHintClick();
    }

    /**
     * Occurs when user clicks on checker piece
     *
     * @param actionEvent event
     */
    private void onPieceClick(ActionEvent actionEvent) {
        if (game.getTurn() == Player.HUMAN) {
            CheckerButton button = (CheckerButton) actionEvent.getSource();
            int pos = button.getPosition();
            if (button.getPiece().getPlayer() == Player.HUMAN) {
                possibleMoves = game.getValidMoves(pos);
                updateCheckerBoard();
                if (possibleMoves.isEmpty()) {
                    MoveFeedback feedback = game.moveFeedbackClick(pos);
                    updateText(feedback.toString());
                    if (feedback == MoveFeedback.FORCED_JUMP) {
                        // show movable jump pieces
                        onHelpMovablesClick();
                    }
                } else {
                    updateText("");
                }
            }
        }
    }

    /**
     * Occurs when user clicks to move checker piece to new (ghost) location.
     *
     * @param actionEvent event
     */
    private void onGhostButtonClick(ActionEvent actionEvent) {
        if (!game.isGameOver() && game.getTurn() == Player.HUMAN) {
            hintMove = null;
            helpMoves = null;
            GhostButton button = (GhostButton) actionEvent.getSource();
            game.playerMove(button.getBoardstate());
            possibleMoves = new ArrayList<>();
            updateCheckerBoard();
            SwingUtilities.invokeLater(() -> {
                aiMove();
                if (game.isGameOver()) {
                    gameOverDialog();
                }
            });
        }
    }

    private void aiMove() {
        // perform AI move
        long startTime = System.nanoTime();
        game.aiMove();
        // compute time taken
        long aiMoveDurationInMs = (long) ((System.nanoTime() - startTime) / 1E6);
        // compute necessary delay time (not less than zero)
        long delayInMs = Math.max(0, SettingsPanel.AiMinPauseDurationInMs - aiMoveDurationInMs);
        // schedule delayed update
        executor.schedule(this::invokeAiUpdate, delayInMs, TimeUnit.MILLISECONDS);
    }

    /**
     * Update checkerboard and trigger new AI move if necessary
     */
    private void invokeAiUpdate() {
        SwingUtilities.invokeLater(() -> {
            updateCheckerBoard();
            if (!game.isGameOver() && game.getTurn() == Player.AI) {
                aiMove();
            } else if (SettingsPanel.hintMode) {
                // in hint mode, display hint after AI move
                onHintClick();
            }
        });
    }

    /**
     * Open dialog for restarting the program.
     */
    private void onRestartClick() {
        Object[] options = {"Yes", "No"};
        int n = JOptionPane.showOptionDialog(this, "Are you sure you want to restart?",
                "Restart game? ",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[1]);
        if (n == 0) {
            start();
        }
    }

    /**
     * Open dialog for quitting the program
     */
    private void onExitClick() {
        Object[] options = {"Yes", "No"};
        int n = JOptionPane.showOptionDialog(this,
                "\nAre you sure you want to leave?",
                "Quit game? ",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[1]);
        if (n == 0) {
            // close logging file
            this.dispose();
            System.exit(0);
        }
    }

    /**
     * Ask human Player whether they want to replay or exit
     */
    private void gameOverDialog() {
        String[] options = {"Yes", "No"};
        int n = JOptionPane.showOptionDialog(this,
                "Do you want to play again?",
                game.getGameOverMessage(),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);
        if (n == 0) {
            this.start();
        } else {
            // Close logging file
            this.dispose();
            System.exit(0);
        }
    }

    private void onRulesClick() {
        JOptionPane.showMessageDialog(this,
                "<html><body><p style='width: 400px;'>" + rulesList + "</p></body></html>",
                "Game Rules",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private static String getRulesList() {
        return new StringBuilder()
                .append("1. Moves are allowed only on the dark squares, so pieces always move diagonally. Single ")
                .append("pieces are always limited to forward moves (toward the opponent). <br /> <br /> ")
                .append("2. A piece making a non-capturing move (not involving a jump) may move only one square. <br /> <br />")
                .append("3. A piece making a capturing move (a jump) leaps over one of the opponent's pieces, landing in a ")
                .append("straight diagonal line on the other side. Only one piece may be captured in a single jump; however, ")
                .append("multiple jumps are allowed during a single turn. <br /> <br />")
                .append("4. When a piece is captured, it is removed from the board. <br /> <br />")
                .append("5. If a player is able to make a capture, there is no option; the jump must be made. If more than ")
                .append("one capture is available, the player is free to choose whichever he or she prefers. <br /> <br />")
                .append("6. When a piece reaches the furthest row from the player who controls that piece, it is crowned and ")
                .append("becomes a king. <br /> <br />")
                .append("7. Kings are limited to moving diagonally but may move both forward and backward. <br /> <br />")
                .append("8. Kings may combine jumps in several directions, forward and backward, on the same turn. Single ")
                .append("pieces may shift direction diagonally during a multiple capture turn, but must always jump")
                .append("forward (toward the opponent).").toString();
    }

    /**
     * Undo the last move
     */
    private void onUndoClick() {
        game.undo();
        updateCheckerBoard();
        if (SettingsPanel.hintMode) {
            onHintClick();
        }
    }
}
