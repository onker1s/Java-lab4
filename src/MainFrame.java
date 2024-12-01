import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
@SuppressWarnings("serial")
public class MainFrame extends JFrame {
    // Начальные размеры окна приложения
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
// Объект диалогового окна для выбора файлов
private JFileChooser fileChooser = null;
    // Пункты меню
    private JCheckBoxMenuItem showAxisMenuItem;
    private JCheckBoxMenuItem showMarkersMenuItem;
    private JCheckBoxMenuItem showPolygonsMenuItem;
    private JCheckBoxMenuItem rotateMenuItem;
    // Компонент-отображатель графика
    private GraphicsDisplay display = new GraphicsDisplay();
    // Флаг, указывающий на загруженность данных графика
    private boolean fileLoaded = false;

    public MainFrame() {
// Вызов конструктора предка Frame
        super("Построение графиков функций на основе заранее подготовленных файлов");
// Установка размеров окна
                setSize(WIDTH, HEIGHT);
        Toolkit kit = Toolkit.getDefaultToolkit();
// Отцентрировать окно приложения на экране
        setLocation((kit.getScreenSize().width - WIDTH)/2,
                (kit.getScreenSize().height - HEIGHT)/2);
// Развѐртывание окна на весь экран
        setExtendedState(MAXIMIZED_BOTH);
// Создать и установить полосу меню
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
// Добавить пункт меню "Файл"
        JMenu fileMenu = new JMenu("Файл");
        menuBar.add(fileMenu);
// Создать действие по открытию файла
        Action openGraphicsAction = new AbstractAction("Открыть файл с графиком") {
        public void actionPerformed(ActionEvent event) {
            if (fileChooser==null) {
                fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(new File("."));
            }
            if (fileChooser.showOpenDialog(MainFrame.this) ==
                    JFileChooser.APPROVE_OPTION)
                openGraphics(fileChooser.getSelectedFile());
        }
    };
    // Добавить соответствующий элемент меню
fileMenu.add(openGraphicsAction);
    // Создать пункт меню "График"
    JMenu graphicsMenu = new JMenu("График");
menuBar.add(graphicsMenu);
    // Создать действие для реакции на активацию элемента "Показыватьоси координат"
    Action showAxisAction = new AbstractAction("Показывать оси координат") {
    public void actionPerformed(ActionEvent event) {
// свойство showAxis класса GraphicsDisplay истина,если элемент меню
// showAxisMenuItem отмечен флажком, и ложь - в противном случае
        display.setShowAxis(showAxisMenuItem.isSelected());
    }
};
showAxisMenuItem = new JCheckBoxMenuItem(showAxisAction);
// Добавить соответствующий элемент в меню
graphicsMenu.add(showAxisMenuItem);
// Элемент по умолчанию включен (отмечен флажком)
showAxisMenuItem.setSelected(true);
// Повторить действия для элемента "Показывать маркеры точек"
Action showMarkersAction = new AbstractAction("Показывать маркеры точек") {
public void actionPerformed(ActionEvent event) {
// по аналогии с showAxisMenuItem
    display.setShowMarkers(showMarkersMenuItem.isSelected());
}
};
showMarkersMenuItem = new JCheckBoxMenuItem(showMarkersAction);
graphicsMenu.add(showMarkersMenuItem);
// Элемент по умолчанию включен (отмечен флажком)
showMarkersMenuItem.setSelected(true);
// Создать действие для реакции на активацию элемента "Выделить области под графиком"
        Action showPolygonsAction = new AbstractAction("Выделить области под графиком") {
            public void actionPerformed(ActionEvent event) {
// свойство showAxis класса GraphicsDisplay истина,если элемент меню
// showAxisMenuItem отмечен флажком, и ложь - в противном случае
                display.setShowPolygons(showPolygonsMenuItem.isSelected());
            }
        };
        showPolygonsMenuItem = new JCheckBoxMenuItem(showPolygonsAction);
// Добавить соответствующий элемент в меню
        graphicsMenu.add(showPolygonsMenuItem);

        // Создать действие для реакции на активацию элемента "Выделить области под графиком"
        Action rotateAction = new AbstractAction("Повернуть") {
            public void actionPerformed(ActionEvent event) {
// свойство showAxis класса GraphicsDisplay истина,если элемент меню
// showAxisMenuItem отмечен флажком, и ложь - в противном случае
                display.setRotateGraph(rotateMenuItem.isSelected());
            }
        };
        rotateMenuItem = new JCheckBoxMenuItem(rotateAction);
// Добавить соответствующий элемент в меню
        graphicsMenu.add(rotateMenuItem);


// Зарегистрировать обработчик событий, связанных с меню "График"
graphicsMenu.addMenuListener(new GraphicsMenuListener());
// Установить GraphicsDisplay в цент граничной компоновки
getContentPane().add(display, BorderLayout.CENTER);
}




    protected void openGraphics(File selectedFile) {
        try {
            // Шаг 1 - Открыть поток чтения данных, связанный с входным файловым потоком
            BufferedReader reader = new BufferedReader(new FileReader(selectedFile));

            // Создаем список для хранения координат
            List<Double[]> graphicsDataList = new ArrayList<>();
            String line;

            // Шаг 2 - Цикл чтения данных
            while ((line = reader.readLine()) != null) {
                String[] values = line.trim().split("\\s+"); // Разделяем строку по пробелам
                if (values.length >= 2) {
                    Double x = Double.parseDouble(values[0]);
                    Double y = Double.parseDouble(values[1]);
                    graphicsDataList.add(new Double[] {x, y});
                }
            }

            // Преобразуем список в массив
            Double[][] graphicsData = new Double[graphicsDataList.size()][2];
            graphicsData = graphicsDataList.toArray(graphicsData);

            // Шаг 3 - Проверка, имеется ли в массиве хотя бы одна пара координат
            if (graphicsData.length > 0) {
                // Да - установить флаг загруженности данных
                fileLoaded = true;

                // Вывод значений в консоль
                for (Double[] point : graphicsData) {
                    System.out.println("X: " + point[0] + ", Y: " + point[1]);
                }

                // Вызвать метод отображения графика
                display.showGraphics(graphicsData);
            } else {
                JOptionPane.showMessageDialog(MainFrame.this, "Файл не содержит данных", "Ошибка загрузки данных", JOptionPane.WARNING_MESSAGE);
            }

            // Шаг 4 - Закрыть входной поток
            reader.close();

        } catch (FileNotFoundException ex) {
            // В случае исключительной ситуации типа "Файл не найден" показать сообщение об ошибке
            JOptionPane.showMessageDialog(MainFrame.this, "Указанный файл не найден", "Ошибка загрузки данных", JOptionPane.WARNING_MESSAGE);
        } catch (IOException ex) {
            // В случае ошибки ввода из файлового потока показать сообщение об ошибке
            JOptionPane.showMessageDialog(MainFrame.this, "Ошибка чтения координат точек из файла", "Ошибка загрузки данных",
                    JOptionPane.WARNING_MESSAGE);
        } catch (NumberFormatException ex) {
            // Обработка ошибок преобразования в Double
            JOptionPane.showMessageDialog(MainFrame.this, "Ошибка формата данных в файле", "Ошибка загрузки данных",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

public static void main(String[] args) {
// Создать и показать экземпляр главного окна приложения
    MainFrame frame = new MainFrame();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);
}
// Класс-слушатель событий, связанных с отображением меню
private class GraphicsMenuListener implements MenuListener {
    // Обработчик, вызываемый перед показом меню
    public void menuSelected(MenuEvent e) {
// Доступность или недоступность элементов меню "График" определяется загруженностью данных
        showAxisMenuItem.setEnabled(fileLoaded);
        showMarkersMenuItem.setEnabled(fileLoaded);
        showPolygonsMenuItem.setEnabled(fileLoaded);
        rotateMenuItem.setEnabled(fileLoaded);
    }
    // Обработчик, вызываемый после того, как меню исчезло с экрана
    public void menuDeselected(MenuEvent e) {
    }
// Обработчик, вызываемый в случае отмены выбора пункта меню(очень редкая ситуация)
    public void menuCanceled(MenuEvent e) {
    }
}
}