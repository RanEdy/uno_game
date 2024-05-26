import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class PlayerDeck extends JPanel {
  private LinkedList<Card> cartasLista;
  private LinkedList<ArrayList<Card>> cartasDistribuidas;
  private ArrayList<MouseListener> mouseListeners;
  private ArrayList<ComponentListener> componentListeners;
  private int index = -1;

  private JPanel deckPanel;
  private JPanel botonesPanel;

  private JButton izq, der;
  private JButton ordenarColor, ordenarTipo;
  private Font botonesFont = new Font("Consolas", Font.BOLD, 20);
  private Color botonesColor = new Color(255, 120, 0);
  private Border botonesBorder = BorderFactory.createLineBorder(new Color(0,0,0, 30), 3, true);
  private Dimension botonesFlechasDimension = new Dimension(90, 230);
  private Dimension botonesOrdenarDimension = new Dimension(30, 30);
  private ImageIcon botonDerIcon = generarImagen(new ImageIcon("iconos/der.png"), botonesFlechasDimension.width, botonesFlechasDimension.height);
  private ImageIcon botonDerIconSelected = generarImagen(new ImageIcon("iconos/der_hover.png"), botonesFlechasDimension.width, botonesFlechasDimension.height);
  private ImageIcon botonIzqIcon = generarImagen(new ImageIcon("iconos/izq.png"), botonesFlechasDimension.width, botonesFlechasDimension.height);
  private ImageIcon botonIzqIconSelected = generarImagen(new ImageIcon("iconos/izq_hover.png"), botonesFlechasDimension.width, botonesFlechasDimension.height);

  private Font miniFont = new Font("Consolas", Font.BOLD, 14);

  private Audio ordenarFx, cambiarFx;

  public PlayerDeck(LinkedList<Card> cartasIniciales) {
    super();
    setLayout(new BorderLayout(20,0));
    setBorder(null);
    setOpaque(false);
    setSize(720, 230);

    deckPanel = new JPanel(null);
    deckPanel.setPreferredSize(new Dimension(720, 200));
    deckPanel.setOpaque(false);
    deckPanel.setBackground(new Color(0,0,0,0));
    deckPanel.setBorder(BorderFactory.createLineBorder(new Color(255,255, 255, 20), 2, true));

    botonesPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    botonesPanel.setOpaque(false);
    botonesPanel.setBackground(new Color(0,0,0,0));
    botonesPanel.setBorder(null);

    JPanel support = new JPanel(new BorderLayout(0,0));
    support.setBorder(BorderFactory.createRaisedBevelBorder());
    support.setBackground(new Color(255,255,0, 30));

    izq = new JButton(botonIzqIcon);
    izq.setFont(botonesFont);
    izq.setBorder(null);
    izq.setContentAreaFilled(false);
    izq.setPreferredSize(botonesFlechasDimension);
    izq.addActionListener((e) -> { 
      cambiarFx = new Audio("sfx/hover1.wav", 0.2f);
      displayPrev5();
      getParent().repaint();
    });
    // Se tiene que hacer esto para repintar el fondo y que se siga viendo transparente (Cosas de Swing xd)
    izq.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseMoved(MouseEvent e) {
        getParent().repaint();
      }

      @Override
      public void mouseEntered(MouseEvent e) {
        izq.setIcon(botonIzqIconSelected);
      }

      @Override
      public void mouseExited(MouseEvent e) {
        izq.setCursor(new Cursor(Cursor.HAND_CURSOR));
        izq.setIcon(botonIzqIcon);
      }
    });


    der = new JButton(botonDerIcon);
    der.setFont(botonesFont);
    der.setBorder(null);
    der.setPreferredSize(botonesFlechasDimension);
    der.setContentAreaFilled(false);
    der.addActionListener((e) -> { 
      cambiarFx = new Audio("sfx/hover1.wav", 0.2f);
      displayNext5();
      getParent().repaint(); 
    });
    // Se tiene que hacer esto para repintar el fondo y que se siga viendo transparente (Cosas de Swing xd)
    der.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseMoved(MouseEvent e) {
        getParent().repaint();
      }

      @Override
      public void mouseEntered(MouseEvent e) {
        der.setCursor(new Cursor(Cursor.HAND_CURSOR));
        der.setIcon(botonDerIconSelected);
      }

      @Override
      public void mouseExited(MouseEvent e) {
        der.setIcon(botonDerIcon);
      }
    });


    ordenarColor = new JButton("C");
    ordenarColor.setFont(miniFont);
    ordenarColor.setBorder(botonesBorder);
    ordenarColor.setBackground(botonesColor);
    ordenarColor.setPreferredSize(botonesOrdenarDimension);
    ordenarColor.addActionListener((e) -> { 
      ordenarFx = new Audio("sfx/swap1.wav", 0.8f);
      ordenarColor(); 
      getParent().repaint(); 
    });

    // Se tiene que hacer esto para repintar el fondo y que se siga viendo transparente (Cosas de Swing xd)
    ordenarColor.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseMoved(MouseEvent e) {
        getParent().repaint();
      }
    });

    ordenarTipo = new JButton("T");
    ordenarTipo.setFont(miniFont);
    ordenarTipo.setBorder(botonesBorder);
    ordenarTipo.setBackground(botonesColor);
    ordenarTipo.setPreferredSize(botonesOrdenarDimension);
    ordenarTipo.addActionListener((e) -> { 
      ordenarFx = new Audio("sfx/swap1.wav", 0.8f);
      ordenarTipo();
      getParent().repaint(); 
    });
    // Se tiene que hacer esto para repintar el fondo y que se siga viendo transparente (Cosas de Swing xd)
    ordenarTipo.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseMoved(MouseEvent e) {
        getParent().repaint();
      }
    });


    botonesPanel.add(ordenarColor);
    botonesPanel.add(ordenarTipo);

    cartasLista = cartasIniciales;
    cartasDistribuidas = new LinkedList<>();
    mouseListeners = new ArrayList<>();
    componentListeners = new ArrayList<>();

    support.add(deckPanel, BorderLayout.CENTER);
    support.add(botonesPanel, BorderLayout.SOUTH);
    add(support, BorderLayout.CENTER);
    add(izq, BorderLayout.WEST);
    add(der, BorderLayout.EAST);
    distribuir();
    displayNext5();
  }

  public void reset(LinkedList<Card> nuevasCartas) {
    cartasDistribuidas.clear();
    //cartasLista.clear();
    cartasLista = (LinkedList<Card>) nuevasCartas.clone();
    updateListeners();
    distribuir();
    index = -1;
    displayNext5();
  }

  public void addCard(Card card) {
    cartasLista.add(card);
    update();
  }

  public void addCard(LinkedList<Card> cards) {
    for(Card card : cards)
      cartasLista.add(card);
    update();
  }

  public Card removeCard(int i) {
    index = 0;
    Card c = cartasLista.remove(i);
    update();
    return c;
  }

  public void removeCard(Card c) {
    index = 0;
    cartasLista.remove(c);
    update();
  }

  // Agrega un mouseListener a todas las cartas del mazo
  public void addCardMouseListener(MouseListener mouseListener) {
    mouseListeners.add(mouseListener);
    update();
  }

  public void addCardComponentListener(ComponentListener componentListener) {
    componentListeners.add(componentListener);
    update();
  }

  public int getListaCartasSize() {
    System.out.println("CartasListaSize: " + cartasLista.size());
    return cartasLista.size();
  }

  public boolean buscarTipo(Card c) {
    for(Card card : cartasLista) {
      if(c.getCardType() == card.getCardType())
        return true;
    }
    return false;
  }

  public boolean buscarValida(Card c) {
    for(Card card : cartasLista)
      if(c.isValid(card))
        return true;
    return false;
  }

  private void updateListeners() {
    for(Card c : cartasLista) {
      for(MouseListener ml : mouseListeners) {
        c.removeMouseListener(ml);
        c.addMouseListener(ml);
      }
      for(ComponentListener cl : componentListeners) {
        c.removeComponentListener(cl);
        c.addComponentListener(cl);
      }
    }
  }

  private void update() {
    deckPanel.removeAll();
    if(cartasLista.size() >= 1) {
      updateListeners();
      distribuir();
      int x = 18;
      for(Card card : cartasDistribuidas.get(index)) {
        card.setBounds(x, 50, card.getWidth(), card.getHeight());
        card.updateOriginalPos();
        deckPanel.add(card);
        x += card.getWidth() +15;
      }
    }
    deckPanel.revalidate();
    deckPanel.repaint();

  }

  private void distribuir() {
    cartasDistribuidas.clear();
    ArrayList<Card> buffer = new ArrayList<>();
    for(int i = 0; i < cartasLista.size(); i++) {
      buffer.add(cartasLista.get(i));
      if(((i+1) % 5 == 0 && i != 0) || (i+1) == cartasLista.size()) {
        cartasDistribuidas.add((ArrayList<Card>)buffer.clone());
        buffer.clear();
      }
    }
  }

  private void displayNext5() {
    if(cartasDistribuidas.size() >= 1) {
      index = (index + 1) % cartasDistribuidas.size();
      update();
    }
  }

  private void displayPrev5() {
    if(cartasDistribuidas.size() >= 1) {
      index--;
      if(index < 0)
        index = cartasDistribuidas.size()-1;
      else
        index = index % cartasDistribuidas.size();
      update();
    }
  }

  private void ordenarColor() {
    Collections.sort(cartasLista, Card.getColorComparator());
    update();
  }

  private void ordenarTipo() {
    Collections.sort(cartasLista, Card.getTypeComparator());
    update();
  }

  private ImageIcon generarImagen(ImageIcon originalIcon, int width, int height) {
    Image originalImg = originalIcon.getImage();
    Image escaladaImg = originalImg.getScaledInstance(width, height, Image.SCALE_SMOOTH);
    return new ImageIcon(escaladaImg);
  }

}
