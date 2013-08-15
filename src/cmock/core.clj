(ns cmock.core
    (:import (org.mockito Mockito)
           (org.mockito.stubbing Answer)))
(defn inside-when-expr [when-code mock-obj]
  (conj (conj (rest when-code) mock-obj )
                  (symbol (str "." (first when-code)))))



(defn when-expr [when-code mock-obj return-code]
  (list '.thenReturn
      (list 'org.mockito.Mockito/when
            (inside-when-expr when-code mock-obj)) return-code))


(defn answer-expr [when-code mock-obj return-code]
  (list '.thenAnswer
      (list 'org.mockito.Mockito/when
            (inside-when-expr when-code mock-obj))
      (list 'reify 'org.mockito.stubbing.Answer (conj (conj (rest return-code) '[_ _]) 'answer))))

(defn throw-expr [when-code mock-obj return-code]
  (concat (conj
    (list
      (list '.when
            (list 'org.mockito.Mockito/doThrow
                  (list (symbol (str (second return-code) "."))))
            mock-obj))
    (symbol (str "." (first when-code)))) (rest when-code))
  )


(defn return-expr [return-code]
  (if (and (list? return-code) (= (first return-code) (symbol "var-args")))
   `(if (list? (rest ~return-code))
      (first (rest ~return-code))
      (into-array Object (rest (rest ~return-code)))
      ~return-code)
   return-code))

(defmacro provided [mock-obj & args]
  (let [p-args (partition 3 args)]
    (conj
     (map (fn [[when-code implies return-code]]
              (assert (= implies (symbol "=>")))
              (cond
                (and (list? return-code) (= (first return-code) (symbol "throws")))
                (throw-expr when-code mock-obj return-code)
                (and (list? return-code) (= (first return-code) (symbol "answer")))
                (answer-expr when-code mock-obj return-code)
                :else
                (when-expr when-code mock-obj return-code)))
            (partition 3 args)) 'do)))
