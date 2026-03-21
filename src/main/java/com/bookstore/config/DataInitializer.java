package com.bookstore.config;

import com.bookstore.entity.*;
import com.bookstore.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CategoryRepository categoryRepository;
    private final BookRepository bookRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final ReviewRepository reviewRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (bookRepository.count() > 0) {
            log.info("Database already seeded — skipping DataInitializer");
            return;
        }
        log.info("Seeding database...");

        Role adminRole = roleRepository.findByName(Role.RoleName.ROLE_ADMIN).orElseThrow();
        Role userRole  = roleRepository.findByName(Role.RoleName.ROLE_USER).orElseThrow();

        Category fiction     = save(cat("Fiction",     "Художественная литература", "Classic and contemporary fiction novels"));
        Category programming = save(cat("Programming", "Программирование", "Software development and computer science"));
        Category science     = save(cat("Science",     "Наука", "Popular science and discoveries"));
        Category biography   = save(cat("Biography",   "Биография", "Life stories of remarkable people"));
        Category history     = save(cat("History",     "История", "World history and historical events"));
        Category selfHelp    = save(cat("Self-Help",   "Саморазвитие", "Personal growth and productivity"));
        Category mystery     = save(cat("Mystery",     "Детективы и триллеры", "Crime, thriller and detective stories"));

        User admin = createUser("admin@bookstore.com", "Admin1234!", "Admin",  "Bookstore", "123 Admin St, New York",      "+375291234567", Set.of(adminRole, userRole));
        User alice = createUser("alice@mail.com",      "Alice1234!", "Alice",  "Johnson",   "456 Oak Ave, Los Angeles",    "+375331234568", Set.of(userRole));
        User bob   = createUser("bob@mail.com",        "Bob12345!",  "Bob",    "Smith",     "789 Pine Rd, Chicago",        "+79161234569", Set.of(userRole));
        User carol = createUser("carol@mail.com",      "Carol123!",  "Carol",  "Williams",  "321 Elm St, Houston",         "+375441234570", Set.of(userRole));
        User david = createUser("david@mail.com",      "David123!",  "David",  "Brown",     "654 Maple Dr, Phoenix",       "+79261234571", Set.of(userRole));
        User emma  = createUser("emma@mail.com",       "Emma1234!",  "Emma",   "Davis",     "987 Cedar Ln, Philadelphia",  "+375291234572", Set.of(userRole));
        User frank = createUser("frank@mail.com",      "Frank123!",  "Frank",  "Miller",    "147 Birch Blvd, San Antonio", "+79031234573", Set.of(userRole));
        User grace = createUser("grace@mail.com",      "Grace123!",  "Grace",  "Wilson",    "258 Walnut St, San Diego",    "+375331234574", Set.of(userRole));
        User henry = createUser("henry@mail.com",      "Henry123!",  "Henry",  "Moore",     "369 Spruce Ave, Dallas",      "+44741234575", Set.of(userRole));
        User ivan  = createUser("ivan@mail.com",       "Ivan1234!",  "Ivan",   "Taylor",    "741 Poplar Rd, San Jose",     "+79991234576", Set.of(userRole));

        // ======= PROGRAMMING =======
        Book cleanCode = save(book("Clean Code", "Чистый код",
                "Robert C. Martin", "Роберт Мартин", "9780132350884", "39.99",
                "A handbook of agile software craftsmanship.",
                "Руководство по написанию чистого, читаемого и поддерживаемого кода.",
                2008,
                Set.of(programming)));

        Book pragProg = save(book("The Pragmatic Programmer", "Программист-прагматик",
                "Andrew Hunt, David Thomas", "Эндрю Хант, Дэвид Томас", "9780135957059", "49.99",
                "Your journey to mastery. Practical advice for becoming a better programmer.",
                "Ваш путь к мастерству. Практические советы для профессионального роста разработчика.",
                2019,
                Set.of(programming)));

        Book designPatterns = save(book("Design Patterns", "Паттерны проектирования",
                "Gang of Four", "Банда четырёх", "9780201633610", "54.99",
                "Elements of reusable object-oriented software. The classic 23 patterns.",
                "Элементы повторно используемого ОО-программного обеспечения. 23 классических паттерна.",
                1994,
                Set.of(programming)));

        Book refactoring = save(book("Refactoring", "Рефакторинг",
                "Martin Fowler", "Мартин Фаулер", "9780134757599", "47.99",
                "Improving the design of existing code.",
                "Улучшение проекта существующего кода. Каталог методов рефакторинга.",
                2018,
                Set.of(programming)));

        Book ddd = save(book("Domain-Driven Design", "Предметно-ориентированное проектирование",
                "Eric Evans", "Эрик Эванс", "9780321125217", "52.99",
                "Tackling complexity in the heart of software.",
                "Борьба со сложностью в программном обеспечении.",
                2003,
                Set.of(programming)));

        Book springInAction = save(book("Spring in Action", "Spring в действии",
                "Craig Walls", "Крейг Уоллс", "9781617294945", "44.99",
                "Comprehensive guide to the Spring Framework.",
                "Исчерпывающее руководство по Spring Framework.",
                2018,
                Set.of(programming)));

        Book cleanArch = save(book("Clean Architecture", "Чистая архитектура",
                "Robert C. Martin", "Роберт Мартин", "9780134494166", "41.99",
                "A craftsman's guide to software structure and design.",
                "Руководство мастера по структуре и дизайну программного обеспечения.",
                2017,
                Set.of(programming)));

        Book javaEffective = save(book("Effective Java", "Эффективная Java",
                "Joshua Bloch", "Джошуа Блох", "9780134685991", "45.99",
                "Best practices for the Java platform. 90 items with concrete recommendations.",
                "Лучшие практики для платформы Java. 90 пунктов с конкретными рекомендациями.",
                2018,
                Set.of(programming)));

        Book introAlgo = save(book("Introduction to Algorithms", "Введение в алгоритмы",
                "Cormen, Leiserson, Rivest", "Кормен, Лейзерсон, Ривест", "9780262033848", "79.99",
                "The comprehensive guide to algorithms.",
                "Полное руководство по алгоритмам. Используется в ведущих университетах мира.",
                2009,
                Set.of(programming)));

        Book kubernetes = save(book("Kubernetes in Action", "Kubernetes в действии",
                "Marko Luksa", "Марко Лукса", "9781617293726", "49.99",
                "Comprehensive guide to Kubernetes.",
                "Исчерпывающее руководство по Kubernetes — платформе оркестрации контейнеров.",
                2019,
                Set.of(programming)));

        // ======= FICTION =======
        Book book1984 = save(book("1984", "1984",
                "George Orwell", "Джордж Оруэлл", "9780451524935", "14.99",
                "A dystopian novel about a totalitarian society.",
                "Антиутопический роман о тоталитарном обществе, где Большой Брат следит за всеми.",
                1949,
                Set.of(fiction)));

        Book gatsby = save(book("The Great Gatsby", "Великий Гэтсби",
                "F. Scott Fitzgerald", "Фрэнсис Скотт Фицджеральд", "9780743273565", "12.99",
                "The story of the fabulously wealthy Jay Gatsby.",
                "История сказочно богатого Джея Гэтсби и его любви к Дейзи Бьюкенен.",
                1925,
                Set.of(fiction)));

        Book mockingbird = save(book("To Kill a Mockingbird", "Убить пересмешника",
                "Harper Lee", "Харпер Ли", "9780061935466", "13.99",
                "A masterwork of honor and injustice in the deep South.",
                "Шедевр о чести и несправедливости на Глубоком Юге во времена Великой депрессии.",
                1960,
                Set.of(fiction)));

        Book braveNew = save(book("Brave New World", "О дивный новый мир",
                "Aldous Huxley", "Олдос Хаксли", "9780060850524", "13.99",
                "A chilling vision of a future society engineered for happiness.",
                "Леденящее видение будущего общества, созданного для счастья, но лишённого свободы.",
                1932,
                Set.of(fiction)));

        Book catcherRye = save(book("The Catcher in the Rye", "Над пропастью во ржи",
                "J.D. Salinger", "Джером Сэлинджер", "9780316769174", "14.99",
                "A teenager's journey through New York City after being expelled from school.",
                "Путешествие подростка по Нью-Йорку после отчисления из школы.",
                1951,
                Set.of(fiction)));

        Book hundredYears = save(book("One Hundred Years of Solitude", "Сто лет одиночества",
                "Gabriel García Márquez", "Габриэль Гарсиа Маркес", "9780060883287", "15.99",
                "The Buendía family's multigenerational story in the mythical town of Macondo.",
                "Многопоколенческая история семьи Буэндиа в мифическом городе Макондо.",
                1967,
                Set.of(fiction)));

        Book lordOfRings = save(book("The Lord of the Rings", "Властелин колец",
                "J.R.R. Tolkien", "Джон Р. Р. Толкин", "9780618640157", "29.99",
                "The epic fantasy journey of Frodo Baggins to destroy the One Ring.",
                "Эпическое фэнтезийное путешествие Фродо Бэггинса с целью уничтожить Единое кольцо.",
                1954,
                Set.of(fiction)));

        Book masterMarg = save(book("The Master and Margarita", "Мастер и Маргарита",
                "Mikhail Bulgakov", "Михаил Булгаков", "9780143108696", "16.99",
                "The Devil visits Soviet Moscow with his entourage.",
                "Дьявол со свитой посещает советскую Москву, сея хаос и обнажая человеческую природу.",
                1967,
                Set.of(fiction)));

        Book crimeAndPun = save(book("Crime and Punishment", "Преступление и наказание",
                "Fyodor Dostoevsky", "Фёдор Достоевский", "9780143058144", "15.99",
                "A poor student commits a murder and struggles with guilt and redemption.",
                "Бедный студент совершает убийство и борется с чувством вины и искуплением.",
                1866,
                Set.of(fiction)));

        Book wAndP = save(book("War and Peace", "Война и мир",
                "Leo Tolstoy", "Лев Толстой", "9781400079988", "19.99",
                "A sweeping epic of Russian society during the Napoleonic Wars.",
                "Масштабный эпос о русском обществе во время Наполеоновских войн.",
                1869,
                Set.of(fiction)));

        // ======= MYSTERY =======
        Book sherlock = save(book("The Adventures of Sherlock Holmes", "Приключения Шерлока Холмса",
                "Arthur Conan Doyle", "Артур Конан Дойл", "9780141040059", "11.99",
                "Twelve classic detective stories featuring the brilliant Sherlock Holmes.",
                "Двенадцать классических детективных историй с блестящим Шерлоком Холмсом.",
                1892,
                Set.of(mystery)));

        Book goneGirl = save(book("Gone Girl", "Исчезнувшая",
                "Gillian Flynn", "Гиллиан Флинн", "9780307588371", "15.99",
                "On a warm summer morning, Amy Dunne disappears.",
                "Тёплым летним утром Эми Данн исчезает. Её муж становится главным подозреваемым.",
                2012,
                Set.of(mystery)));

        Book daVinci = save(book("The Da Vinci Code", "Код да Винчи",
                "Dan Brown", "Дэн Браун", "9780307474278", "14.99",
                "A murder in the Louvre leads to a conspiracy threatening Christianity.",
                "Убийство в Лувре ведёт к заговору, угрожающему христианству.",
                2003,
                Set.of(mystery)));

        Book girl = save(book("The Girl with the Dragon Tattoo", "Девушка с татуировкой дракона",
                "Stieg Larsson", "Стиг Ларссон", "9780307454546", "15.99",
                "A journalist and a hacker investigate a decades-old disappearance.",
                "Журналист и хакер расследуют многолетнее исчезновение в богатой семье.",
                2005,
                Set.of(mystery)));

        Book agatha = save(book("Murder on the Orient Express", "Убийство в «Восточном экспрессе»",
                "Agatha Christie", "Агата Кристи", "9780062693662", "12.99",
                "Hercule Poirot investigates a murder on a snowbound train.",
                "Эркюль Пуаро расследует убийство в занесённом снегом поезде.",
                1934,
                Set.of(mystery)));

        // ======= SCIENCE =======
        Book briefHistory = save(book("A Brief History of Time", "Краткая история времени",
                "Stephen Hawking", "Стивен Хокинг", "9780553380163", "17.99",
                "From the Big Bang to Black Holes.",
                "От Большого взрыва до чёрных дыр. Хокинг объясняет вселенную доступным языком.",
                1988,
                Set.of(science)));

        Book cosmos = save(book("Cosmos", "Космос",
                "Carl Sagan", "Карл Саган", "9780345539434", "18.99",
                "A personal voyage through the universe.",
                "Личное путешествие по вселенной в поисках места человечества в космосе.",
                1980,
                Set.of(science)));

        Book sapiens = save(book("Sapiens", "Sapiens: Краткая история человечества",
                "Yuval Noah Harari", "Юваль Ной Харари", "9780062316097", "19.99",
                "A brief history of humankind from Stone Age to the present.",
                "Краткая история человечества — от каменного века до наших дней.",
                2011,
                Set.of(science, history)));

        Book origin = save(book("The Origin of Species", "Происхождение видов",
                "Charles Darwin", "Чарльз Дарвин", "9780140432053", "12.99",
                "Darwin's groundbreaking work on the theory of evolution.",
                "Основополагающий труд Дарвина о теории эволюции путём естественного отбора.",
                1859,
                Set.of(science)));

        Book blackHole = save(book("Black Hole Survival Guide", "Руководство по выживанию в чёрной дыре",
                "Janna Levin", "Джанна Левин", "9781524748920", "21.99",
                "An accessible exploration of black holes.",
                "Доступное исследование чёрных дыр — самых экстремальных объектов во вселенной.",
                2020,
                Set.of(science)));

        // ======= BIOGRAPHY =======
        Book steveJobs = save(book("Steve Jobs", "Стив Джобс",
                "Walter Isaacson", "Уолтер Айзексон", "9781451648539", "22.99",
                "The exclusive biography of Apple's co-founder.",
                "Эксклюзивная биография сооснователя Apple, основанная на более чем 40 интервью.",
                2011,
                Set.of(biography)));

        Book elonMusk = save(book("Elon Musk", "Илон Маск",
                "Walter Isaacson", "Уолтер Айзексон", "9781982181284", "28.99",
                "An intimate look at the world's most controversial entrepreneur.",
                "Взгляд изнутри на самого неоднозначного предпринимателя мира и его компании.",
                2023,
                Set.of(biography)));

        Book longWalk = save(book("Long Walk to Freedom", "Долгий путь к свободе",
                "Nelson Mandela", "Нельсон Мандела", "9780316548182", "21.99",
                "The autobiography of Nelson Mandela and his fight against apartheid.",
                "Автобиография Нельсона Манделы и его борьба с апартеидом.",
                1994,
                Set.of(biography, history)));

        Book diary = save(book("The Diary of a Young Girl", "Дневник молодой девушки",
                "Anne Frank", "Анна Франк", "9780553296983", "11.99",
                "Anne Frank's moving account of hiding during the Nazi occupation.",
                "Трогательный рассказ Анны Франк о скрывании во время нацистской оккупации.",
                1947,
                Set.of(biography, history)));

        Book openBook = save(book("Open", "Открытый",
                "Andre Agassi", "Андре Агасси", "9780307388407", "18.99",
                "The remarkable autobiography of tennis champion Andre Agassi.",
                "Замечательная автобиография одного из величайших теннисных чемпионов.",
                2009,
                Set.of(biography)));

        // ======= HISTORY =======
        Book homoDeus = save(book("Homo Deus", "Homo Deus: Краткая история будущего",
                "Yuval Noah Harari", "Юваль Ной Харари", "9780062464316", "19.99",
                "A brief history of tomorrow.",
                "Краткая история завтрашнего дня — что ждёт человечество в следующем столетии.",
                2015,
                Set.of(history, science)));

        Book guns = save(book("Guns, Germs, and Steel", "Ружья, микробы и сталь",
                "Jared Diamond", "Джаред Даймонд", "9780393317558", "18.99",
                "Why did Western civilization dominate the world?",
                "Почему западная цивилизация завоевала мир? Географический и исторический ответ.",
                1997,
                Set.of(history, science)));

        Book silkRoads = save(book("The Silk Roads", "Шёлковый путь",
                "Peter Frankopan", "Питер Франкопан", "9781101912379", "19.99",
                "A new history of the world through trade routes.",
                "Новая история мира через торговые пути, соединявшие цивилизации.",
                2015,
                Set.of(history)));

        // ======= SELF-HELP =======
        Book atomicHabits = save(book("Atomic Habits", "Атомные привычки",
                "James Clear", "Джеймс Клир", "9780735211292", "21.99",
                "An easy and proven way to build good habits and break bad ones.",
                "Простой и проверенный способ выработать хорошие привычки и избавиться от плохих.",
                2018,
                Set.of(selfHelp)));

        Book thinking = save(book("Thinking, Fast and Slow", "Думай медленно... решай быстро",
                "Daniel Kahneman", "Даниэль Канеман", "9780374533557", "17.99",
                "A psychologist's exploration of the two systems that drive our thinking.",
                "Исследование о двух системах мышления, управляющих нашими решениями.",
                2011,
                Set.of(selfHelp, science)));

        Book power = save(book("The Power of Now", "Сила настоящего",
                "Eckhart Tolle", "Экхарт Толле", "9781577314806", "16.99",
                "A guide to spiritual enlightenment and living in the present moment.",
                "Руководство по духовному просветлению и жизни в настоящем моменте.",
                1997,
                Set.of(selfHelp)));

        Book rich = save(book("Rich Dad Poor Dad", "Богатый папа, бедный папа",
                "Robert Kiyosaki", "Роберт Кийосаки", "9781612680194", "15.99",
                "What the rich teach their kids about money.",
                "Чему богатые учат своих детей о деньгах, чего не делают бедные и средний класс.",
                1997,
                Set.of(selfHelp)));

        Book sevenHabits = save(book("The 7 Habits of Highly Effective People", "7 навыков высокоэффективных людей",
                "Stephen R. Covey", "Стивен Кови", "9780743269513", "18.99",
                "Powerful lessons in personal change.",
                "Мощные уроки личностных изменений. Одна из самых влиятельных бизнес-книг.",
                1989,
                Set.of(selfHelp)));

        Book deepWork = save(book("Deep Work", "В работу с головой",
                "Cal Newport", "Кэл Ньюпорт", "9781455586691", "19.99",
                "Rules for focused success in a distracted world.",
                "Правила успеха в мире постоянных отвлечений. Как достичь максимальной продуктивности.",
                2016,
                Set.of(selfHelp)));

        Book subtle = save(book("The Subtle Art of Not Giving a F*ck", "Тонкое искусство пофигизма",
                "Mark Manson", "Марк Мэнсон", "9780062457714", "17.99",
                "A counterintuitive approach to living a good life.",
                "Нетривиальный подход к хорошей жизни — заботиться о меньшем количестве вещей.",
                2016,
                Set.of(selfHelp)));

        Book mindset = save(book("Mindset", "Образ мышления",
                "Carol S. Dweck", "Кэрол Дуэк", "9780345472328", "16.99",
                "The new psychology of success.",
                "Новая психология успеха — как научиться реализовывать свой потенциал.",
                2006,
                Set.of(selfHelp)));

        Book grit = save(book("Grit", "Твёрдость характера",
                "Angela Duckworth", "Анджела Дакворт", "9781501111105", "17.99",
                "The power of passion and perseverance.",
                "Сила страсти и упорства. Почему таланта недостаточно для успеха.",
                2016,
                Set.of(selfHelp)));

        Book ikigai = save(book("Ikigai", "Икигай",
                "Héctor García, Francesc Miralles", "Эктор Гарсия, Франсеск Миральес", "9780143130727", "15.99",
                "The Japanese secret to a long and happy life.",
                "Японский секрет долгой и счастливой жизни. Поиск причины просыпаться каждое утро.",
                2016,
                Set.of(selfHelp)));

        log.info("Created {} books", bookRepository.count());

        // ======= ОТЗЫВЫ =======
        addReview(alice, cleanCode,    5, "Лучшая книга о написании кода!");
        addReview(bob,   cleanCode,    4, "Отличные принципы, хотя некоторые примеры устарели.");
        addReview(carol, cleanCode,    5, "Полностью изменила мой подход к написанию кода.");
        addReview(alice, pragProg,     5, "Практические советы которые реально работают.");
        addReview(david, pragProg,     4, "Много полезных идей о профессиональном росте.");
        addReview(emma,  designPatterns, 4, "Классика. Паттерны объяснены чётко.");
        addReview(frank, designPatterns, 3, "Полезно, но читается тяжело.");
        addReview(grace, atomicHabits, 5, "Изменила мою жизнь! Простые концепции с огромным эффектом.");
        addReview(henry, atomicHabits, 5, "Лучшая книга по продуктивности. Перечитываю каждый год.");
        addReview(ivan,  atomicHabits, 4, "Очень практичная. Сразу применяю советы в жизни.");
        addReview(bob,   book1984,     5, "Пугающе актуальная книга даже сегодня.");
        addReview(carol, book1984,     5, "Оруэлл предвидел многое из того что происходит сейчас.");
        addReview(alice, sapiens,      5, "Меняет взгляд на историю и место человека во вселенной.");
        addReview(david, sapiens,      4, "Очень интересно, хотя некоторые выводы спорны.");
        addReview(emma,  sapiens,      5, "Одна из лучших нон-фикшн книг!");
        addReview(frank, steveJobs,    4, "Интересная биография, хотя Джобс показан неоднозначно.");
        addReview(grace, steveJobs,    5, "Невероятно вдохновляющая история о бизнесе и инновациях.");
        addReview(henry, gatsby,       4, "Красивый язык, хотя история немного меланхолична.");
        addReview(ivan,  gatsby,       5, "Шедевр американской литературы.");
        addReview(bob,   thinking,     5, "Канеман объясняет как мы принимаем решения.");
        addReview(carol, thinking,     4, "Сложновато, но очень важные идеи о мышлении.");
        addReview(alice, deepWork,     5, "Полностью изменила подход к работе.");
        addReview(emma,  deepWork,     4, "Важная книга в эпоху постоянных отвлечений.");
        addReview(david, lordOfRings,  5, "Толкин создал целый мир с историей, языками и мифологией.");
        addReview(frank, lordOfRings,  5, "Лучшее фэнтези всех времён. Перечитывал три раза!");
        addReview(grace, masterMarg,   5, "Булгаков — гений. Каждый раз открываю что-то новое.");
        addReview(henry, crimeAndPun,  5, "Достоевский заставляет думать о природе добра и зла.");

        log.info("Database seeded: {} users, {} books, 27 reviews",
                userRepository.count(), bookRepository.count());
    }

    private Category save(Category c) { return categoryRepository.save(c); }
    private Category cat(String name, String nameRu, String desc) {
        return Category.builder().name(name).nameRu(nameRu).description(desc).build();
    }

    private Book save(Book b) { return bookRepository.save(b); }
    private Book book(String title, String titleRu,
                      String author, String authorRu,
                      String isbn, String price,
                      String description, String descriptionRu,
                      Integer publishedYear,
                      Set<Category> categories) {
        return Book.builder()
                .title(title).titleRu(titleRu)
                .author(author).authorRu(authorRu)
                .isbn(isbn).price(new BigDecimal(price))
                .description(description).descriptionRu(descriptionRu)
                .publishedYear(publishedYear)
                .categories(categories)
                .build();
    }

    private User createUser(String email, String password, String firstName,
                            String lastName, String address, String phone, Set<Role> roles) {
        User user = User.builder()
                .email(email).password(passwordEncoder.encode(password))
                .firstName(firstName).lastName(lastName)
                .shippingAddress(address).phone(phone).roles(roles).build();
        User saved = userRepository.save(user);
        ShoppingCart cart = new ShoppingCart();
        cart.setUser(saved);
        shoppingCartRepository.save(cart);
        return saved;
    }

    private void addReview(User user, Book book, int rating, String comment) {
        reviewRepository.save(Review.builder()
                .user(user).book(book).rating(rating).comment(comment).build());
    }
}