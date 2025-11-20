

import components.rotor.Rotor;
import components.rotor.RotorManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnigmaConsoleTest {

    public static void main(String[] args) {
        // --- 1. הגדרת האלפבית (ABCDEF) ומיפוי תו לאינדקס (0-5) ---
        // האלפבית: A, B, C, D, E, F (אורך 6)
        Map<Character, Integer> charToIndex = new HashMap<>();
        charToIndex.put('A', 0); charToIndex.put('B', 1); charToIndex.put('C', 2);
        charToIndex.put('D', 3); charToIndex.put('E', 4); charToIndex.put('F', 5);
        int alphabetLength = 6;

        // --- 2. יצירת רוטור 1 (פנימית - מיפוי בין אינדקסים) ---
        // בדוגמה: Right(A->F) Left(C->D) -> כל רוטור ממפה אינדקס לאינדקס
        // אם נניח שהחיווט הפנימי הוא קבוע בין האינדקסים:
        // (A(0) -> D(3)) ; (B(1) -> E(4)) ; (C(2) -> F(5)) וכו'
        // ***יש צורך ליצור את המפה מתוך הגדרת ה-XML המלאה של הרוטורים***
        // לצורך בדיקה ראשונית, נשתמש במפה פנימית כלשהי:
        // חיווט רוטור 1: 0->5, 1->4, 2->3, 3->2, 4->1, 5->0
        Map<Integer, Integer> wiring1 = new HashMap<>();
        wiring1.put(0, 5); wiring1.put(1, 4); wiring1.put(2, 3);
        wiring1.put(3, 2); wiring1.put(4, 1); wiring1.put(5, 0);

        // חיווט רוטור 2: 0->5, 1->1, 2->4, 3->2, 4->0, 5->3
        Map<Integer, Integer> wiring2 = new HashMap<>();
        wiring2.put(0, 5); wiring2.put(1, 1); wiring2.put(2, 4);
        wiring2.put(3, 2); wiring2.put(4, 0); wiring2.put(5, 3);

        Rotor r1 = new Rotor(1, wiring1, 4, alphabetLength); // Notch=4
        System.out.println("rotor1 created" + "\n");
        // --- 3. יצירת רוטור 2 (פנימית - מיפוי בין אינדקסים) ---
       // נניח ש-Rotor 2 ממפה 0->3, 1->4, 2->5, 3->0, 4->1, 5->2 (מתוך sanity-small)
        Rotor r2 = new Rotor(2, wiring2, 2, alphabetLength); // Notch=2
        System.out.println("rotor1 created" + "\n");

        Map<Integer, Rotor> allRotors = new HashMap<>();
        allRotors.put(1, r1);
        allRotors.put(2, r2);
        System.out.println("allRotors map<int, rotor> created" + "\n");

        // --- 4. יצירת RotorManager ---
        RotorManager manager = new RotorManager(allRotors, alphabetLength, charToIndex);
        System.out.println("rotor manager created" + "\n");

        // --- 5. קביעת סדר הרוטורים (2 משמאל, 1 מימין) --
        List<Integer> rotorOrder = new ArrayList<>();
        rotorOrder.add(2); // Left
        rotorOrder.add(1); // Right
        manager.setRotorsOrder(rotorOrder);
        System.out.println("rotor order " + "\n");

        System.out.println("setting rotors position " + "\n");
        // --- 6. קביעת מיקומים התחלתיים (CC) ---
        List<Character> positions = new ArrayList<>();
        positions.add('C'); // Rotor 2 (Left) position
        positions.add('C'); // Rotor 1 (Right) position
        manager.setRotorsPositions(positions); // מצב 2 בתרשים


        // --- 7. מעבר למצב 3 (פסיעה לפני הקלדת C) ---
        // כאן מגיע שלב הפסיעה שנדרש לפני קידוד
        // הרוטורים צריכים להיות במצב 3 מהתרשים שלכם
        manager.moveRotorsBeforeEncodingLetter();

        // --- 8. קידוד האות C (מצב 4) ---
        // נבדוק רק את המסלול מהרוטור הימני (1) אל הרוטור השמאלי (2) - החלק ה-"Forward"
        int inputIndex = charToIndex.get('B'); // B הוא הקלט האפקטיבי אחרי לוח תקעים (B|C)

        System.out.println("--- Encoding B -> A ---");
        System.out.println("Input Index (B): " + inputIndex);

        // שימו לב: קידוד זה (RTL) צריך להשתמש ב-mapForward
        // נשתמש בגרסה מתוקנת של הלולאה ההפוכה
        // int outputIndex = manager.encryptLetterThroughRotorsRTL(inputIndex);

        // כרגע נשתמש בלולאה ידנית:
        int signal = inputIndex;
        // מעבר RTL
        signal = manager.encryptLetterThroughRotorsRTL(signal);

        // --- 9. הדפסת תוצאה ---
        char outputChar = getChar(signal, charToIndex);
        System.out.println("Output Index: " + signal);
        System.out.println("Encoded Character before reflector: " + outputChar);

        signal = 2;
        signal = manager.encryptLetterThroughRotorsLTR(signal);

        char outputChar2 = getChar(signal, charToIndex);
        System.out.println("Output Index: " + signal);
        System.out.println("Final Encoded Character: " + outputChar);

    }

    // פונקציית עזר פשוטה להמרת אינדקס לתו
    private static char getChar(int index, Map<Character, Integer> charToIndex) {
        for (Map.Entry<Character, Integer> entry : charToIndex.entrySet()) {
            if (entry.getValue().equals(index)) {
                return entry.getKey();
            }
        }
        return '?';
    }
}