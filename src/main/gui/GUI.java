package main.gui;

import main.game.*;
import main.game.Settings;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.swing.*;

public class GUI extends JFrame{

    private Game game;
    private ArrayList<BoardState> possibleMoves;
    private SquarePanel[] squares;
    private JPanel checkerboardPanel;
    private JPanel contentPane;
    private JTextArea textBox;
    // hint feature
    private BoardState hintMove;
    private List<Integer> helpMoves;
    private HashMap<Integer, Integer> difficultyMapping;

    public GUI(){
        difficultyMapping = new HashMap<>();
        difficultyMapping.put(1,1);
        difficultyMapping.put(2, 5);
        difficultyMapping.put(3, 8);
        difficultyMapping.put(4, 12);
        start();
    }

    private void start(){
        settingsPopup();
        game = new Game();
        possibleMoves = new ArrayList<>();
        hintMove = null;
        setup();
        if (main.gui.Settings.hintMode){
            onHintClick();
        }
    }

    /**
     * Pop up dialog for user to choose game settings (e.g. AI difficulty, starting player etc)
     */
    private void settingsPopup(){
        // panel for options
        JPanel panel = new JPanel(new GridLayout(5,1));
        // difficulty slider
        JLabel text1 = new JLabel("Set Difficulty", 10);
        JSlider slider = new JSlider();
        slider.setSnapToTicks(true);
        slider.setMaximum(4);
        slider.setMinimum(1);
        slider.setMajorTickSpacing(1);
        Hashtable<Integer, JLabel> labels = new Hashtable<Integer, JLabel>();
        labels.put(1, new JLabel("Easy"));
        labels.put(2, new JLabel("Medium"));
        labels.put(3, new JLabel("Hard"));
        labels.put(4, new JLabel("On Fire"));
        slider.setLabelTable(labels);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setPreferredSize(new Dimension(200,50));
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
        aiRadioButton.setSelected(Settings.FIRSTMOVE== Player.AI);
        humanFirstRadioButton.setSelected(Settings.FIRSTMOVE== Player.HUMAN);
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
        if(result == JOptionPane.OK_OPTION){
            Settings.AI_DEPTH =  difficultyMapping.get(slider.getValue());
            System.out.println("AI depth = " + Settings.AI_DEPTH);
            Settings.FIRSTMOVE = humanFirstRadioButton.isSelected() ? Player.HUMAN : Player.AI;
            Settings.FORCETAKES = forceTakesButton.isSelected();
        }
    }


    /**
     * Sets up initial GUI configuration.
     */
    public void setup()
    {
        switch (Settings.FIRSTMOVE){
            case AI:
                main.gui.Settings.AIcolour = Colour.WHITE;
                break;
            case HUMAN:
                main.gui.Settings.AIcolour = Colour.BLACK;
                break;
        }

        setupMenuBar();
        contentPane = new JPanel();
        checkerboardPanel = new JPanel(new GridBagLayout());
        JPanel textPanel = new JPanel();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));
        this.setContentPane(contentPane);
        contentPane.add(checkerboardPanel);
        contentPane.add(textPanel);
        textBox = new JTextArea();
        textBox.setEditable(false);
        textBox.setLineWrap(false);
        textBox.setWrapStyleWord(true);
        textBox.setAutoscrolls(true);
        textPanel.add(textBox);

        updateCheckerBoard();
        updateText("");
        this.pack();
        this.setVisible(true);
        if (Settings.FIRSTMOVE == Player.AI){
            aiMove();
        }
    }

    private void updateText(String text){
        textBox.setText(text);
    }

    /**
     * Updates the checkerboard GUI based on the game state.
     */
    private void updateCheckerBoard(){
        checkerboardPanel.removeAll();
        addPieces();
        addSquares();
        addGhostButtons();
        checkerboardPanel.setVisible(true);
        checkerboardPanel.repaint();
        this.pack();
        this.setVisible(true);
    }

    private void addSquares(){
        squares = new SquarePanel[game.getState().NO_SQUARES];
        int fromPos = -1;
        int toPos = -1;
        if(hintMove != null){
            fromPos = hintMove.getFromPos();
            toPos = hintMove.getToPos();
        }
        GridBagConstraints c = new GridBagConstraints();
        for (int i = 0; i < game.getState().NO_SQUARES; i++){
            c.gridx = i % game.getState().SIDE_LENGTH;
            c.gridy = i / game.getState().SIDE_LENGTH;
            squares[i] = new SquarePanel(c.gridx, c.gridy);
            if (i == fromPos){
                squares[i].setHighlighted();
            }
            if(i == toPos){
                squares[i].setHighlighted();
            }
            if (helpMoves != null){
                if (helpMoves.contains(i)){
                    squares[i].setHighlighted();
                }
            }
            checkerboardPanel.add(squares[i], c);
        }
    }


    /**
     * Add checker pieces to the GUI corresponding to the game state
     */
    private void addPieces(){
        GridBagConstraints c = new GridBagConstraints();
        for (int i = 0; i < game.getState().NO_SQUARES; i++){
            c.gridx = i % game.getState().SIDE_LENGTH;
            c.gridy = i / game.getState().SIDE_LENGTH;
            if(game.getState().getPiece(i) != null){
                Piece piece = game.getState().getPiece(i);
                CheckerButton button = new CheckerButton(i, piece, this);
                button.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        onPieceClick(actionEvent);
                    }
                });
                checkerboardPanel.add(button, c);
            }
        }
    }

    /**
     * Add "ghost buttons" showing possible moves for the player
     */
    private void addGhostButtons(){
        for (BoardState state : possibleMoves){
            int newPos = state.getToPos();
            GhostButton button = new GhostButton(state);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    onGhostButtonClick(actionEvent);
                }
            });
            squares[newPos].add(button);
        }
    }


    /**
     * Sets up the menu bar component.
     */
    private void setupMenuBar(){

        // ensure exit method is called on window closing
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(
                new WindowAdapter() {
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
        viewItemHelpMode.setSelected(main.gui.Settings.helpMode);
        viewItemHintMode.setSelected(main.gui.Settings.hintMode);
        JMenu helpMenu = new JMenu("Help");
        JMenuItem rulesItem = new JMenuItem("Game Rules");
        JMenuItem helpItemHint = new JMenuItem("Hint!");
        JMenuItem helpItemMovables = new JMenuItem("Show movable pieces");

        // add action listeners
        quitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                onExitClick();
            }
        });
        restartItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                onRestartClick();
            }
        });
        rulesItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                onRulesClick();
            }
        });
        undoItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                onUndoClick();
            }
        });
        viewItemHelpMode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                onHelpModeClick();
            }
        });
        viewItemHintMode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                onHintModeClick();
            }
        });
        helpItemHint.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                onHintClick();
            }
        });
        helpItemMovables.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                onHelpMovablesClick();
            }
        });


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

    /***************************************************************/
    /*********************** ON CLICK METHODS **********************/

    public void onMouseRelease(int position, int dx, int dy){
        MoveFeedback feedback = game.playerMove(position, dx, dy);
        if (feedback == MoveFeedback.SUCCESS){
            updateCheckerBoard();
            aiMove();
        }
        else{
            updateCheckerBoard();
            System.out.println(feedback.toString());
        }
    }

    private void onHintClick(){
        if (!game.isGameOver()){
            AI ai = new AI(10, Player.HUMAN);
            helpMoves = null;
            hintMove = ai.move(this.game.getState(), Player.HUMAN);
            updateCheckerBoard();
        }
    }

    private void onHelpMovablesClick(){
        hintMove = null;
        helpMoves = game.getState().getSuccessors().stream().map(x -> x.getFromPos()).collect(Collectors.toList());
        updateCheckerBoard();
    }

    private void onHelpModeClick(){
        main.gui.Settings.helpMode = !main.gui.Settings.helpMode;
        System.out.println("help mode: " + main.gui.Settings.helpMode);
    }

    private void onHintModeClick(){
        main.gui.Settings.hintMode = !main.gui.Settings.hintMode;
        System.out.println("hint mode: " + main.gui.Settings.hintMode);
        onHintClick();
    }

    /**
     * Occurs when user clicks on checker piece
     * @param actionEvent
     */
    private void onPieceClick(ActionEvent actionEvent){
        if(game.getTurn() == Player.HUMAN ){
            CheckerButton button = (CheckerButton) actionEvent.getSource();
            int pos = button.getPosition();
            if(button.getPiece().getPlayer() == Player.HUMAN){
                possibleMoves = game.getValidMoves(pos);
                updateCheckerBoard();
                if (possibleMoves.size() == 0){
                    MoveFeedback feedback = game.moveFeedbackClick(pos);
                    updateText(feedback.toString());
                    if (feedback == MoveFeedback.FORCED_JUMP){
                        // show movable jump pieces
                        onHelpMovablesClick();
                    }
                }
                else{
                    updateText("");
                }
            }
        }
    }

    /**
     * Occurs when user clicks to move checker piece to new (ghost) location.
     * @param actionEvent
     */
    private void onGhostButtonClick(ActionEvent actionEvent){
        if (!game.isGameOver() && game.getTurn() == Player.HUMAN){
            hintMove = null;
            helpMoves = null;
            GhostButton button = (GhostButton) actionEvent.getSource();
            game.playerMove(button.getBoardstate());
            possibleMoves = new ArrayList<>();
            updateCheckerBoard();
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    aiMove();
                    if (game.isGameOver()){
                        gameOver();
                    }
                }
            });
        }
    }

    private void gameOver(){
        JOptionPane.showMessageDialog(this,
                game.getGameOverMessage(),
                "",
                JOptionPane.INFORMATION_MESSAGE );
    }

    private void aiMove(){
        // perform AI move
        long startTime = System.nanoTime();
        game.aiMove();
        // compute time taken
        long aiMoveDurationInMs = (System.nanoTime() - startTime)/1000000;
        // compute necessary delay time (not less than zero)
        long delayInMs = Math.max(0, main.gui.Settings.AiMinPauseDurationInMs - aiMoveDurationInMs);
        // schedule delayed update
        ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);
        exec.schedule(new Runnable(){
            @Override
            public void run(){
                invokeAiUpdate();
            }
        }, delayInMs, TimeUnit.MILLISECONDS);
    }

    /**
     * Update checkerboard and trigger new AI move if necessary
     */
    private void invokeAiUpdate(){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                updateCheckerBoard();
                if (!game.isGameOver() && game.getTurn() == Player.AI){
                    aiMove();
                }
                else if (main.gui.Settings.hintMode){
                    // in hint mode, display hint after AI move
                    onHintClick();
                }
            }
        });
    }

    /**
     * Open dialog for restarting the program.
     */
    private void onRestartClick()
    {
        Object[] options = {"Yes",
                "No", };
        int n = JOptionPane.showOptionDialog(this, "Are you sure you want to restart?",
                "Restart game? ",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[1]);
        if (n == 0){
            start();
        }
    }

    /**
     * Open dialog for quitting the program
     */
    private void onExitClick(){
        Object[] options = {"Yes",
                "No", };
        int n = JOptionPane.showOptionDialog(this,
                        "\nAre you sure you want to leave?",
                "Quit game? ",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[1]);
        if (n == 0){
            // close logging file
            this.dispose();
            System.exit(0);
        }
    }

    /**
     * Open help dialog.
     */
    private void onRulesClick(){

        String message =
                "1. Moves are allowed only on the dark squares, so pieces always move diagonally. Single " +
                "pieces are always limited to forward moves (toward the opponent). <br /> <br /> " +

                "2. A piece making a non-capturing move (not involving a jump) may move only one square. <br /> <br />"+

                "3. A piece making a capturing move (a jump) leaps over one of the opponent's pieces, landing in a " +
                "straight diagonal line on the other side. Only one piece may be captured in a single jump; however, " +
                "multiple jumps are allowed during a single turn. <br /> <br />" +

                        "4. When a piece is captured, it is removed from the board. <br /> <br />" +
                "5. If a player is able to make a capture, there is no option; the jump must be made. If more than " +
                   "one capture is available, the player is free to choose whichever he or she prefers. <br /> <br />" +

                "6. When a piece reaches the furthest row from the player who controls that piece, it is crowned and " +
                        "becomes a king. <br /> <br />" +
                "7. Kings are limited to moving diagonally but may move both forward and backward. <br /> <br />" +
                "8. Kings may combine jumps in several directions, forward and backward, on the same turn. Single " +
                        "pieces may shift direction diagonally during a multiple capture turn, but must always jump " +
                        "forward (toward the opponent).";

        JOptionPane.showMessageDialog(this,
                "<html><body><p style='width: 400px;'>"+message+"</p></body></html>",
                "",
                JOptionPane.INFORMATION_MESSAGE );
    }

    /**
     * Undo the last move
     */
    private void onUndoClick(){
        game.undo();
        updateCheckerBoard();
        if (main.gui.Settings.hintMode){
            onHintClick();
        }
    }
}
