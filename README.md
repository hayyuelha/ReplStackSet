# ReplStackSet

Tugas IF4031 Pengembangan Aplikasi Terdistribusi
"Implementasi Replicated Stack and Set pada JGroups"

Hayyu' Luthfi Hanifah (13512080)
Choirunnisa Fatima (13512084)

##Deskripsi
Implementasi replicated stack di atas JGroups yang memiliki antar muka minimal sebagai berikut:
```
public class ReplStack<T> {
	public void push(T obj);
	public T pop();
	public T top();
}
```

Implementasi replicated stack di atas JGroups yang memiliki antar muka minimal sebagai berikut:
```
public class ReplSet<T> {

	public boolean add(T obj);
	/** mengembalikan true jika obj ditambahkan,
		dan false jika obj telah ada pada set */
	
	public boolean contains(T obj);
	/** mengembalikan true jika obj ada pada set */
	
	public boolean remove(T obj);
	/** mengembalikan true jika obj ada pada set, dan
		kemudian obj dihapus dari set. Mengembalikan false
		jika obj tidak ada pada set */
}
```

##Petunjuk Instalasi/Building
Pada root direktori, jalankan perintah `mvn clean install` 

##Petunjuk Menjalankan Program
Untuk menjalankan Replicated Stack, jalankan perintah `mvn exec:java -Dexec.mainClass="replicated_stack_set.stack_set.ReplStack"`.

Untuk menjalankan Replicated Set, jalankan perintah `mvn exec:java -Dexec.mainClass="replicated_stack_set.stack_set.ReplSet"`.

##Test untuk Replicated Stack
Untuk menjalankan pengujian, jalankan perintah `mvn test`.
Pengujian dilakukan dengan membuat dua objek kelas `ReplicatedStack`, yaitu `stack1` dan `stack2`.
 
Berikut adalah skenario pengujian yang dilakukan:

1. Method `push`
	- Kasus normal
		1. Lakukan `stack1.push(5)`
		2. Periksa bahwa `set1` sama dengan `set2`

2. Method `top`
	- Kasus normal
		1. Lakukan `stack1.top()` dan `stack2.top()`
		2. Hasil dari 1 seharusnya adalah `5`

3. Method `pop`
	- Kasus normal
		1. Lakukan `stack1.push(6)`, lalu `stack2.pop()`
		2. Hasil dari pop seharusnya adalah `6`
		3. Periksa bahwa top dari kedua stack adalah `5`

##Test untuk Replicated Set
Untuk menjalankan pengujian, jalankan perintah `mvn test`.
Pengujian dilakukan dengan membuat dua objek kelas `ReplicatedSet`, yaitu `set1` dan `set2`.
 
Berikut adalah skenario pengujian yang dilakukan:

1. Method `add`
	- Kasus normal
		1. Lakukan `set1.add(5)`
		2. Hasil dari 1 seharusnya adalah `true`
		3. Periksa bahwa `set1` sama dengan `set2`
	- Kasus elemen sudah ada di set dan penambahan dilakukan oleh `set2`
		1. Lakukan kembali `set2.add(5)`
		2. Hasil dari 1 seharusnya adalah `false`

2. Method `contains`
	- Kasus elemen ada di set
		1. Lakukan `set1.contains(5)` dan `set2.contains(5)`
		2. Hasil dari 1 seharusnya adalah `true`
	- Kasus elemen tidak ada di set
		1. Lakukan `set1.contains(6)` dan `set2.contains(6)`
		2. Hasil dari 1 seharusnya adalah `false`

3. Method `remove`
	- Kasus elemen tidak ada di set
		1. Lakukan `set2.remove(6)`
		2. Hasil dari 1 seharusnya adalah `false`
	- Kasus normal
		1. Lakukan `set2.remove(5)`
		2. Hasil dari 1 seharusnya adalah `true`
		3. Periksa bahwa elemen sudah dihapus dari `set2` 
		4. Periksa bahwa `set1` sama dengan `set2`