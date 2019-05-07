(ns scratch.core
  (:import (java.awt Color Dimension)
           (javax.swing JPanel JFrame)
           (java.awt.event ActionListener KeyListener)))

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))

(defn game-panel [frame]
  (proxy [JPanel ActionListener KeyListener] []
    (paintComponent [g]
      (proxy-super paintComponent g)
      (. g drawString "Hi There" 10 10))
    (actionPerformed [e]
      (.repaint this))
    (keyPressed [e]
      (println e))
    (keyReleased [e])
    (keyTyped [e])
    (getPreferredSize []
      (Dimension. 300 300))))

(defn game []
  (let [frame (JFrame. "Test")
        panel (game-panel frame)]
    (doto panel
      (.setFocusable true)
      (.setBackground Color/green)
      (.addKeyListener panel))
    (doto frame
      (.add panel)
      (.pack)
      (.setResizable false)
      (.setVisible true))))


                      
