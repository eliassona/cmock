(ns cmock.tests
  (:use [cmock.core]
        [clojure.test]))



(deftest macro-expansion
  (is (= '(do (.thenReturn (org.mockito.Mockito/when (.exists f)) true) (.thenReturn (org.mockito.Mockito/when (.getPath f)) "path"))
         (macroexpand-1
          '(provided f
                     (exists) => true
                     (getPath) => "path")))
      ))
