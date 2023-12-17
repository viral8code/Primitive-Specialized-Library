## IntegerMap
int型特化のHashMapです。HashMap<Integer,Integer>に対応します。  
java.util.HashMapに寄せるために連鎖法を用いた他、各LinkedListの長さがテキトーな閾値を超えるとAVL木に変換します。
