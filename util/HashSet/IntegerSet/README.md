## IntegerSet
int型特化のHashSetです。  
java.util.HashSetに寄せるために連鎖法を用いた他、各LinkedListの長さがテキトーな閾値を超えるとAVL木に変換します。
