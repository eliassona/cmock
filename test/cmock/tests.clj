(ns cmock.tests
  (:use [cmock.core]
        [clojure.test])
  (:import (org.mockito Mockito)
           (java.io File))
  )



(deftest macro-expansion
  (is (= '(do (.thenReturn (org.mockito.Mockito/when (.exists f)) true) (.thenReturn (org.mockito.Mockito/when (.getPath f)) "path"))
         (macroexpand-1
          '(provided f
                     (exists) => true
                     (getPath) => "path")))
      ))



(deftest actual-mocking
  (let [f (Mockito/mock File)]
    (is (not (.exists f)))
    (is (= nil (.getPath f)))
    (is (not (.setReadable f false true)))
    (provided f
              (exists) => true
              (getPath) => "path"
              (setReadable false true) => true)

    (is (.exists f))
    (is (= "path" (.getPath f)))
    (is (.setReadable f false true))))
