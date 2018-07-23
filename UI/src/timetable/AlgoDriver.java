package timetable;

import javafx.scene.control.Alert;

import java.util.ArrayList;
import java.io.*;

public class AlgoDriver{
    static ArrayList<Room> rooms;
    static ArrayList<Clss> classes;
    public static  void main(String[] args){
        DbDriver dbDriver = new DbDriver();
        rooms = dbDriver.getRooms();
        classes = dbDriver.getClasses();
        //create timetable by assigning each class a room
        for(Clss c: classes) {
            //System.out.println(c.getU().getCode()+" "+c.getClassCount()+" "+c.getC().getName()+" "+c.getC().getYear());
            assignRoom(c);
        }
        saveRooms();
        getRoomFromFile();
        // Test code
        Clss[][] slots = rooms.get(0).getDays();
        for(Clss c : slots[0])
            System.out.print("SLOT\t\t");
        System.out.println();
        for(Room r: rooms){
            System.out.println("\n\n"+r.getName());
            Clss[][] s = r.getDays();
            for(int i = 0; i < s.length; i++) {
                for (int j = 0; j < s[i].length; j++)
                    if(s[i][j] != null)
                        System.out.print(s[i][j].getU().getCode() + "\t\t");
                    else
                        System.out.print(null + "\t\t");
                System.out.println();
            }

        }
    }
    private static void assignRoom(Clss c){
        //loop through rooms until an appropriate class is found then assign

        ONE:
        for(int k = 0; k < rooms.size(); k++){
            if(c.getClassCount() <= 0) return;
            Clss[][] days = rooms.get(k).getDays();
            TWO:
            for(int i = 0; i < days.length; i++) {
                //if class already in day move to next day
                if(classInDay(c, days[i])) continue TWO;
                for (int j = 0; j < days[i].length; j++) {
                    if (days[i][j] == null && !conflict(c, i, j, rooms.get(k))) {//also ensure no conflicts
                        days[i][j] = c;
                        c.decClassCount();
                        //System.out.println(c.getClassCount());
                        continue ONE;
                    }
                }
            }
        }
    }
    private static boolean classInDay(Clss c, Clss[] day){
        for(int i = 0; i < day.length; i++)
            if(day[i] == c)
                return true;
        return false;
    }
    private static boolean conflict(Clss c, int i, int j, Room room){
        for(Room r: rooms){
            if(r == room) break;
            Clss[][] classes = r.getDays();
            if(classes[i][j] == null) continue;
            else if(classes[i][j].getC() == c.getC()) return true;
        }
        return false;
    }
    private static void saveRooms(){
        //store the time table in a file
        try(
                ObjectOutputStream fileOut = new ObjectOutputStream(new FileOutputStream("timetable.dat"))
        ){
            fileOut.writeObject(rooms);
        }catch(FileNotFoundException ex){
            ex.printStackTrace();
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }
    private static void getRoomFromFile(){
        //restore the timetable from file.
        try(
                ObjectInputStream fileOut = new ObjectInputStream(new FileInputStream("timetable.dat"))
        ){
            rooms = (ArrayList<Room>) fileOut.readObject();
        }catch(FileNotFoundException ex){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("file system error error");
            alert.setContentText("couldn't locate the database file");
            alert.showAndWait();
            ex.printStackTrace();
        }catch(IOException ex){
            ex.printStackTrace();
        }catch (ClassNotFoundException ex){
            ex.printStackTrace();
        }
    }
}