package main.gui;

import main.game.*;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

public class GUI extends JFrame{

    private Game game;
    private ArrayList<BoardState> possibleMoves;

    // gui components
    private JPanel contentPane;
    private JOptionPane optionPane;
    private SquarePanel[] squares;

    public GUI(){
        start();
    }

    private void start(){
        game = new Game();
        possibleMoves = new ArrayList<>();
        setup();
    }

    /**
     * Sets up initial GUI configuration.
     */
    public void setup()
    {
        setupMenuBar();
        updateCheckerBoard();
        this.pack();
        this.setVisible(true);
    }

    /**
     * Updates the checkerboard GUI based on the game state.
     */
    private void updateCheckerBoard(){
        GridBagConstraints c = new GridBagConstraints();
        contentPane = new JPanel(new GridBagLayout());
        squares = new SquarePanel[game.getState().NO_SQUARES];
        for (int i = 0; i < game.getState().NO_SQUARES; i++){
            c.gridx = i % game.getState().SIDE_LENGTH;
            c.gridy = i / game.getState().SIDE_LENGTH;
            squares[i] = new SquarePanel(c.gridx, c.gridy);
            contentPane.add(squares[i], c);
        }
        addPieces();
        addGhostButtons();
        this.setContentPane(contentPane);
        this.pack();
        this.setVisible(true);
    }

    /**
     * Add checker pieces to the GUI corresponding to the game state
     */
    private void addPieces(){
        for (int i = 0; i < game.getState().NO_SQUARES; i++){
            if(game.getState().getPlayer(i) != null){
                Player player = game.getState().getPlayer(i);
                CheckerButton button = new CheckerButton(i, player);
                button.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        onPieceClick(actionEvent);
                    }
                });
                squares[i].add(button);
            }
        }
    }

    /**
     * Add "ghost buttons" showing possible moves for the player
     */
    private void addGhostButtons(){
        for (BoardState state : possibleMoves){
            int newPos = state.getNewPos();
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
        JMenuItem helpItem = new JMenuItem("Help");
        JMenuItem quitItem = new JMenuItem("Quit");
        JMenu editMenu = new JMenu("Edit");
        JMenuItem undoItem = new JMenuItem("Undo");

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
        helpItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                onHelpClick();
            }
        });
        undoItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                onUndoClick();
            }
        });
        // add components to menu bar
        fileMenu.add(restartItem); fileMenu.add(helpItem); fileMenu.add(quitItem);
        menuBar.add(fileMenu);
        editMenu.add(undoItem);
        menuBar.add(editMenu);
        this.setJMenuBar(menuBar);
    }

    /***************************************************************/
    /*********************** ON CLICK METHODS **********************/

    /**
     * Occurs when user clicks on checker piece
     * @param actionEvent
     */
    private void onPieceClick(ActionEvent actionEvent){
        CheckerButton button = (CheckerButton) actionEvent.getSource();
        int pos = button.getPosition();
        Player player = button.getPlayer();
        ArrayList<BoardState> successors = this.game.getState().getSuccessors(player, pos);
        possibleMoves = new ArrayList<>();
        for (BoardState successor : successors){
            possibleMoves.add(successor);
        }
        updateCheckerBoard();
    }

    /**
     * Occurs when user clicks to move checker piece to new (ghost) location.
     * @param actionEvent
     */
    private void onGhostButtonClick(ActionEvent actionEvent){
        GhostButton button = (GhostButton) actionEvent.getSource();
        game.updateState(button.getBoardstate());
        possibleMoves = new ArrayList<>();
        updateCheckerBoard();
    }

    /**
     * Open dialog for restarting the program.
     */
    private void onRestartClick()
    {
        Object[] options = {"Yes",
                "No", };
        int n = optionPane.showOptionDialog(this, "Are you sure you want to restart?",
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
        int n = optionPane.showOptionDialog(this,
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
    private void onHelpClick(){

    }

    /**
     * Undo the last move
     */
    private void onUndoClick(){
        game.undo();
        updateCheckerBoard();
    }
}
