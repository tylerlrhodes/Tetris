(ns scratch.core
  (:gen-class)
  (:require [scratch.import-static :as is])
  (:import (java.awt Color Dimension)
           (javax.swing JPanel JFrame Timer)
           (java.awt.event ActionListener KeyListener WindowEvent)))

(is/import-static java.awt.event.KeyEvent VK_LEFT VK_RIGHT VK_UP VK_DOWN)

(def game-infra nil)
(def rows 20)
(def cols 10)
(def board (atom (vec (repeat rows (vec (repeat cols 0))))))
(def piece (atom {:type 0 :position [0 0] :orientation 0}))
(def next-piece (atom {:type 0 :orientation 0}))
(def new-piece? (atom true))

(def offset 20)
(def piece-width 20)
(def padding 2)
(def board-width (* (+ piece-width padding) cols))
(def board-height (* (+ piece-width padding) rows))
(def game-width (+ (* offset 2) (+ board-width (* (+ piece-width padding) 6) (* padding 6))))
(def game-height (+ (* offset 2) board-height))
(def next-piece-offset (+ (* offset 2) board-width))
(def bg-color (Color. 230 230 230))

(def pieces
  [{:type 0
    :rotations
    [{:orientation 0
      :matrix
      [[0 0 1 0]
       [0 0 1 0]
       [0 0 1 0]
       [0 0 1 0]]}
     {:orientation 1
      :matrix
      [[0 0 0 0]
       [0 0 0 0]
       [1 1 1 1]
       [0 0 0 0]]}
     {:orientation 2
      :matrix
      [[0 1 0 0]
       [0 1 0 0]
       [0 1 0 0]
       [0 1 0 0]]}
     {:orientation 3
      :matrix
      [[0 0 0 0]
       [1 1 1 1]
       [0 0 0 0]
       [0 0 0 0]]}]}])

(defn get-piece-matrix
  [t o]
  (:matrix ((:rotations (pieces t)) o)))

(defn get-piece-width
  [t]
  (count ((get-piece-matrix t 0) 0)))

(defn get-starting-location
  [piece]
  ;; get the width of the piece
  (let [piece-width (get-piece-width (:type piece))]
    [(/ (- cols piece-width) 2) 0]))


;; How to handle the piece and the board?

;; When the game starts the board is empty
;; We need to generate the first piece (or take it from next-piece)
;; We determine the starting position of the piece
;; We check if there is a collision with the new piece and the board
;; if there is it's game over!

;; At any time the current piece can be rotated via a keypress
;; We need to see if rotating it in the given direction will cause a collision
;; in which case we don't do the rotation in the given direction

;; At any time the current piece can be moved down, left, or right
;; If there is a collision to the left or right, the piece cannot move in that direction
;; If there is a collision with the piece to the bottom, the piece is frozen on the board
;; and the board is updated to reflect the new piece being held in place

;; After the board is updated due to a downward collision, the board
;; is scanned to see if any complete row(s) have been made
;; if so 
(defn update-board []
  (if (@new-piece?)
    ; swap next piece with piece
                                        ; update @piece to starting location)
    nil
  ))


(defn paint-next-piece [g]
  (.translate g next-piece-offset offset)
  (.drawString g "Next Piece:" 0 0)
  (.translate g 0 offset)
  (let [piece @next-piece
        orientation 0
        type (:type piece)
        matrix (get-piece-matrix type orientation)]
    (dotimes [x (count (matrix 0))]
      (dotimes [y (count matrix)]
        (if (= 1 ((matrix y) x))
          (.fillRect g (* x (+ padding piece-width)) (* y (+ padding piece-width)) piece-width piece-width)))))
  (.translate g 0 (- offset))
  (.translate g (- next-piece-offset) (- offset)))

(defn paint-board [g]
  (.translate g offset offset)
  (doseq [x (range cols)
          y (range rows)]
    (.drawRect g (* x (+ padding piece-width)) (* y (+ padding piece-width)) piece-width piece-width))
  (.translate g (- offset) (- offset)))

(defn paint-game [g]
  (paint-board g)
  (paint-next-piece g))

(defn game-panel [frame]
  (proxy [JPanel ActionListener KeyListener] []
    (paintComponent [g]
      (proxy-super paintComponent g)
      (paint-game g))
    (actionPerformed [e]
      (-> (.getSource e)
          (.stop))
      (update-board)
      (-> (.getSource e)
          (.start))
      (.repaint this))
    (keyPressed [e]
      (println e))
    (keyReleased [e])
    (keyTyped [e])
    (getPreferredSize []
      (Dimension. game-width game-height))))

(defn game []
  (let [frame (JFrame. "Test")
        panel (game-panel frame)
        timer (Timer. 1000 panel)]
    (doto panel
      (.setFocusable true)
      (.setBackground bg-color)
      (.addKeyListener panel))
    (doto frame
      (.add panel)
      (.pack)
      (.setResizable false)
      (.setVisible true))
    (.start timer)
    [frame panel timer]))

(defn start-game []
  (let [[frame panel timer] (game)]
    (def game-infra {:timer timer :frame frame :panel panel})))
    
(defn stop-game []
  (.stop (:timer game-infra))
  (.dispatchEvent (:frame game-infra) (WindowEvent. (:frame game-infra) WindowEvent/WINDOW_CLOSING)))

(defn -main []
  (game))

                      
