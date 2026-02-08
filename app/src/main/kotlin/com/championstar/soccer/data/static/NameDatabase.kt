package com.championstar.soccer.data.static

/**
 * NameDatabase
 *
 * A massive collection of first and last names categorized by region.
 * Used to generate unique and culturally appropriate names for thousands of players.
 */
object NameDatabase {

    val regions = listOf("Europe", "SouthAmerica", "Asia", "Africa", "NorthAmerica")

    // --- First Names ---
    val firstNamesEurope = listOf(
        "Liam", "Noah", "Oliver", "Elijah", "James", "William", "Benjamin", "Lucas", "Henry", "Alexander",
        "Mason", "Michael", "Ethan", "Daniel", "Jacob", "Logan", "Jackson", "Levi", "Sebastian", "Mateo",
        "Jack", "Owen", "Theodore", "Aiden", "Samuel", "Joseph", "John", "David", "Wyatt", "Matthew",
        "Luke", "Asher", "Carter", "Julian", "Grayson", "Leo", "Jayden", "Gabriel", "Isaac", "Lincoln",
        "Anthony", "Hudson", "Dylan", "Ezra", "Thomas", "Charles", "Christopher", "Jaxon", "Maverick", "Josiah",
        "Isaiah", "Andrew", "Elias", "Joshua", "Nathan", "Caleb", "Ryan", "Adrian", "Miles", "Eli",
        "Nolan", "Christian", "Aaron", "Cameron", "Ezekiel", "Colton", "Luca", "Landon", "Hunter", "Jonathan",
        "Santiago", "Axel", "Easton", "Cooper", "Jeremiah", "Angel", "Roman", "Connor", "Jameson", "Robert",
        "Greyson", "Jordan", "Ian", "Carson", "Jaxson", "Leonardo", "Nicholas", "Dominic", "Austin", "Everett",
        "Brooks", "Xavier", "Kai", "Jose", "Parker", "Adam", "Jace", "Wesley", "Kayden", "Silas",
        "Bennett", "Declan", "Waylon", "Weston", "Evan", "Emmett", "Micah", "Ryder", "Beau", "Damian",
        "Brayden", "Gael", "Rowan", "Harrison", "Bryson", "Sawyer", "Amir", "Kingston", "Jason", "Giovanni",
        "Vincent", "Ayden", "Chase", "Myles", "Diego", "Nathaniel", "Legend", "Jonah", "River", "Tyler",
        "Cole", "Braxton", "George", "Ashton", "Luis", "Jasper", "Kaiden", "Adriel", "Gavin", "Bentley",
        "Calvin", "Zion", "Juan", "Maxwell", "Max", "Ryker", "Carlos", "Emmanuel", "Jayce", "Lorenzo",
        "Ivan", "Jude", "August", "Kevin", "Malachi", "Elliott", "Rhett", "Archer", "Karter", "Arthur",
        "Luka", "Elliot", "Thiago", "Brandon", "Camden", "Justin", "Jesus", "Maddox", "King", "Theo",
        "Enzo", "Matteo", "Emiliano", "Dean", "Hayden", "Finn", "Brody", "Antonio", "Abel", "Alex",
        "Tristan", "Graham", "Zayden", "Judah", "Xander", "Miguel", "Atlas", "Messiah", "Barrett", "Tucker",
        "Timothy", "Alan", "Edward", "Leon", "Dawson", "Eric", "Ace", "Victor", "Abraham", "Nicolas",
        "Jesse", "Charlie", "Patrick", "Walker", "Joel", "Richard", "Beckett", "Blake", "Alejandro", "Avery",
        "Grant", "Peter", "Oscar", "Milo", "Kaden", "Kyrie", "Bryan", "Melvin", "Lukas", "Jake", "Paul", "Hugo"
    )

    val firstNamesSouthAmerica = listOf(
        "Santiago", "Mateo", "Matías", "Sebastián", "Benjamín", "Martín", "Nicolás", "Alejandro", "Lucas", "Diego",
        "Joaquín", "Samuel", "Gabriel", "Daniel", "Tomás", "Emiliano", "Felipe", "Agustín", "Maximiliano", "Bautista",
        "Juan", "José", "Carlos", "Luis", "Jorge", "Fernando", "Pedro", "Miguel", "Ángel", "Javier",
        "Ricardo", "Francisco", "David", "Manuel", "Antonio", "Roberto", "Eduardo", "Andrés", "Oscar", "Rubén",
        "Sergio", "Mario", "Alberto", "Pablo", "Ramón", "Héctor", "Raúl", "Jesús", "Julio", "César",
        "Víctor", "Hugo", "Gustavo", "Guillermo", "Alfredo", "Enrique", "Cristian", "Marcos", "Arturo", "Adrián",
        "Esteban", "Ramiro", "Ezequiel", "Lautaro", "Facundo", "Ignacio", "Valentín", "Santino", "Thiago", "Gael",
        "Dante", "Lorenzo", "Bruno", "Simón", "Ciro", "Bastián", "Alonso", "Luciano", "Julián", "Jerónimo",
        "Fausto", "Tadeo", "Salvador", "León", "Vicente", "Renato", "Máximo", "Félix", "Camilo", "Emilio",
        "Cristóbal", "Damián", "Ismael", "Rafael", "Mauricio", "Rodrigo", "Gonzalo", "Iván", "Fabio", "Bernardo",
        "Neymar", "Vinícius", "Rodrygo", "Richarlison", "Casemiro", "Alisson", "Ederson", "Marquinhos", "Thiago", "Dani",
        "Marcelo", "Philippe", "Coutinho", "Firmino", "Gabriel", "Jesus", "Lucas", "Paquetá", "Fred", "Fabinho",
        "Raphinha", "Antony", "Bruno", "Guimarães", "Danilo", "Alex", "Sandro", "Bremer", "Éder", "Militão",
        "Weverton", "Pedro", "Everton", "Ribeiro", "Arrascaeta", "Suárez", "Cavani", "Núñez", "Valverde", "Bentancur",
        "Araújo", "Giménez", "Godín", "Muslera", "Torreira", "Vecino", "De la Cruz", "Pellistri", "Olivera", "Viña",
        "Sánchez", "Vidal", "Bravo", "Medel", "Aránguiz", "Isla", "Vargas", "Pulgar", "Maripán", "Díaz",
        "Ospina", "Cuadrado", "Rodríguez", "Falcao", "Zapata", "Muriel", "Mina", "Sánchez", "Barrios", "Uribe"
    )

    val firstNamesAsia = listOf(
        "Wei", "Jie", "Hao", "Yi", "Jun", "Feng", "Lei", "Yang", "Min", "Qiang",
        "Bin", "Gang", "Ping", "Bo", "Hui", "Xin", "Jian", "Hong", "Tao", "Ming",
        "Haruto", "Yuto", "Sota", "Yuki", "Hayato", "Riku", "Hiroto", "Ren", "Yuma", "Itsuki",
        "Kaito", "Asahi", "Hinata", "Minato", "Arata", "Yamato", "Tatsuki", "Kosuke", "Taiga", "Ryota",
        "Sho", "Ken", "Daiki", "Takumi", "Kenta", "Naoki", "Kazuki", "Ryo", "Tsubasa", "Hikaru",
        "Ji-hoon", "Hyun-woo", "Sang-hoon", "Sung-min", "Min-su", "Jun-ho", "Dong-hyun", "Min-ho", "Kyung-soo", "Young-jae",
        "Seo-jun", "Ha-joon", "Do-yun", "Eun-woo", "Si-woo", "Ji-ho", "Ye-jun", "Yu-jun", "Joo-won", "Min-jun",
        "Arif", "Budi", "Dwi", "Eko", "Fajar", "Gilang", "Hendra", "Indra", "Joko", "Kurniawan",
        "Lestari", "Muhammad", "Nur", "Putra", "Rahmat", "Satria", "Tri", "Utama", "Wahyu", "Yuda",
        "Ali", "Ahmed", "Mohammad", "Hassan", "Hussein", "Omar", "Yusuf", "Ibrahim", "Mustafa", "Abdullah",
        "Saeed", "Khalid", "Fahad", "Nasser", "Abdul", "Rahman", "Saleh", "Salem", "Hamad", "Sultan",
        "Ravi", "Rahul", "Amit", "Suresh", "Vijay", "Anil", "Sunil", "Raj", "Deepak", "Sanjay",
        "Arjun", "Aditya", "Rohan", "Vikram", "Karan", "Manish", "Prakash", "Ajay", "Nitin", "Sandeep"
    )

    val firstNamesAfrica = listOf(
        "Mohammed", "Ahmed", "Ali", "Omar", "Yusuf", "Ibrahim", "Mustafa", "Abdullah", "Hassan", "Hussein",
        "Kwame", "Kofi", "Yaw", "Adom", "Boateng", "Mensah", "Owusu", "Appiah", "Asamoah", "Gyan",
        "Chinedu", "Emeka", "Ifeanyi", "Chukwudi", "Obinna", "Uche", "Nnamdi", "Tochukwu", "Kelechi", "Chisom",
        "Oluwaseun", "Ayodele", "Babajide", "Folorunsho", "Gbenga", "Idris", "Jamiu", "Kayode", "Lateef", "Mubarak",
        "Sadio", "Kalidou", "Idrissa", "Cheikhou", "Ismaila", "M'Baye", "Boulaye", "Pape", "Famara", "Habib",
        "Riyad", "Islam", "Aissa", "Ramy", "Sofiane", "Youcef", "Baghdad", "Rais", "Djamel", "Adlene",
        "Hakim", "Noussair", "Romain", "Yassine", "Sofyan", "Azzedine", "Selim", "Achraf", "Munir", "Youssef",
        "Didier", "Yaya", "Kolo", "Wilfried", "Gervinho", "Seydou", "Max", "Franck", "Serge", "Eric",
        "Pierre", "Emerick", "Mario", "Denis", "Bruno", "Andre", "Jordan", "Thomas", "Jeffrey", "Gideon"
    )

    // --- Last Names ---
    val lastNamesEurope = listOf(
        "Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis", "Rodriguez", "Martinez",
        "Hernandez", "Lopez", "Gonzalez", "Wilson", "Anderson", "Thomas", "Taylor", "Moore", "Jackson", "Martin",
        "Lee", "Perez", "Thompson", "White", "Harris", "Sanchez", "Clark", "Ramirez", "Lewis", "Robinson",
        "Walker", "Young", "Allen", "King", "Wright", "Scott", "Torres", "Nguyen", "Hill", "Flores",
        "Green", "Adams", "Nelson", "Baker", "Hall", "Rivera", "Campbell", "Mitchell", "Carter", "Roberts",
        "Müller", "Schmidt", "Schneider", "Fischer", "Weber", "Meyer", "Wagner", "Becker", "Schulz", "Hoffmann",
        "Dubois", "Durand", "Leroy", "Moreau", "Simon", "Laurent", "Lefebvre", "Michel", "Garcia", "David",
        "Rossi", "Russo", "Ferrari", "Esposito", "Bianchi", "Romano", "Colombo", "Ricci", "Marino", "Greco",
        "Silva", "Santos", "Ferreira", "Pereira", "Oliveira", "Costa", "Rodrigues", "Martins", "Jesus", "Sousa",
        "Ivanov", "Smirnov", "Kuznetsov", "Popov", "Vasiliev", "Petrov", "Sokolov", "Mikhailov", "Novikov", "Fedorov",
        "Johansson", "Andersson", "Karlsson", "Nilsson", "Eriksson", "Larsson", "Olsson", "Persson", "Svensson", "Gustafsson",
        "Hansen", "Jensen", "Nielsen", "Pedersen", "Andersen", "Christensen", "Larsen", "Sørensen", "Rasmussen", "Jørgensen",
        "Korhonen", "Virtanen", "Mäkinen", "Nieminen", "Mäkelä", "Hämäläinen", "Laine", "Heikkinen", "Koskinen", "Järvinen",
        "Nagy", "Kovács", "Tóth", "Szabó", "Horváth", "Varga", "Kiss", "Molnár", "Németh", "Farkas",
        "Nowak", "Kowalski", "Wiśniewski", "Wójcik", "Kowalczyk", "Kamiński", "Lewandowski", "Zieliński", "Szymański", "Woźniak"
    )

    val lastNamesSouthAmerica = listOf(
        "Silva", "Santos", "Sousa", "Oliveira", "Pereira", "Lima", "Ferreira", "Costa", "Rodrigues", "Almeida",
        "Nascimento", "Alves", "Araújo", "Ribeiro", "Carvalho", "Gomes", "Martins", "Barbosa", "Ramos", "Melo",
        "Gonzalez", "Rodriguez", "Gomez", "Fernandez", "Lopez", "Diaz", "Martinez", "Perez", "Garcia", "Sanchez",
        "Romero", "Sosa", "Torres", "Ruiz", "Ramirez", "Flores", "Benitez", "Acosta", "Medina", "Herrera",
        "Suarez", "Rojas", "Reyes", "Aguilar", "Mendoza", "Castillo", "Ortiz", "Moreno", "Vargas", "Castro",
        "Gutierrez", "Chavez", "Vasquez", "Jimenez", "Rivera", "Munoz", "De la Cruz", "Salazar", "Guzman", "Villalba",
        "Caceres", "Cardozo", "Galeano", "Ayala", "Vera", "Paredes", "Villamayor", "Samaniego", "Benitez", "Amarilla",
        "Mamani", "Quispe", "Condori", "Choque", "Vargas", "Ramos", "Flores", "Gutierrez", "Cruz", "Chavez"
    )

    val lastNamesAsia = listOf(
        "Li", "Wang", "Zhang", "Liu", "Chen", "Yang", "Zhao", "Huang", "Zhou", "Wu",
        "Xu", "Sun", "Hu", "Zhu", "Gao", "Lin", "He", "Guo", "Ma", "Luo",
        "Sato", "Suzuki", "Takahashi", "Tanaka", "Watanabe", "Ito", "Yamamoto", "Nakamura", "Kobayashi", "Kato",
        "Yoshida", "Yamada", "Sasaki", "Yamaguchi", "Matsumoto", "Inoue", "Kimura", "Hayashi", "Saito", "Shimizu",
        "Kim", "Lee", "Park", "Choi", "Jung", "Kang", "Cho", "Yoon", "Jang", "Lim",
        "Han", "Oh", "Seo", "Shin", "Kwon", "Hwang", "Ahn", "Song", "Jeon", "Hong",
        "Nguyen", "Tran", "Le", "Pham", "Hoang", "Huynh", "Phan", "Vu", "Dang", "Bui",
        "Do", "Ho", "Ngo", "Duong", "Ly", "Singh", "Kumar", "Sharma", "Patel", "Verma"
    )

    val lastNamesAfrica = listOf(
        "Mensah", "Osei", "Asante", "Boateng", "Owusu", "Appiah", "Agyemang", "Kwarteng", "Opoku", "Amoah",
        "Acheampong", "Danso", "Abban", "Baah", "Boadu", "Darko", "Donkor", "Frimpong", "Gyamfi", "Konadu",
        "Nwachukwu", "Okafor", "Okeke", "Okonkwo", "Okoro", "Okoye", "Okpara", "Okun", "Onuoha", "Onye",
        "Diallo", "Sow", "Diop", "Ndiaye", "Fall", "Faye", "Gueye", "Ba", "Seck", "Thiam",
        "Traore", "Keita", "Coulibaly", "Kone", "Toure", "Diakite", "Sidibe", "Sissoko", "Doumbia", "Fofana",
        "Camara", "Bangoura", "Sylla", "Diallo", "Bah", "Barry", "Soumah", "Keita", "Kante", "Conde",
        "Tesfaye", "Bekele", "Alemu", "Kebede", "Getachew", "Berhanu", "Abebe", "Hailu", "Tekle", "Wolde",
        "Moyo", "Ncube", "Sibanda", "Dube", "Ndlovu", "Mpofu", "Nkomo", "Nyathi", "Sithole", "Khumalo"
    )

    // --- Generation Functions ---

    /**
     * Generates a random full name based on region probability.
     */
    fun generateName(): String {
        val region = regions.random()
        return generateNameForRegion(region)
    }

    fun generateNameForRegion(region: String): String {
        val firstName = when (region) {
            "Europe" -> firstNamesEurope.random()
            "SouthAmerica" -> firstNamesSouthAmerica.random()
            "Asia" -> firstNamesAsia.random()
            "Africa" -> firstNamesAfrica.random()
            "NorthAmerica" -> firstNamesEurope.random() // Reuse Europe for now or add distinct list
            else -> firstNamesEurope.random()
        }

        val lastName = when (region) {
            "Europe" -> lastNamesEurope.random()
            "SouthAmerica" -> lastNamesSouthAmerica.random()
            "Asia" -> lastNamesAsia.random()
            "Africa" -> lastNamesAfrica.random()
            "NorthAmerica" -> lastNamesEurope.random()
            else -> lastNamesEurope.random()
        }

        return "$firstName $lastName"
    }
}
