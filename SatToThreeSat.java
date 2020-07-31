import java.util.*;
import java.io.BufferedReader;
import java.io.*;

public class SatToThreeSat {

    public static int numSatVars = 0;
    public static int numSatClauses = 0;
    public static int clauseCount = 0;
    public static ArrayList<String> satClauses = new ArrayList<String>();
    public static int clauseBuilderSat = 0;
    public static boolean newClause = true;
    public static boolean commentPhase = true;
    public static boolean problemPhase = false;
    public static boolean clausePhase = false;

    public static void main(String args[]){
        sattothreesat();
    }

    public static void sattothreesat(){
        //Scan each line entered into stdin
        //interpret each line scanned
        // use global arraylist to then reduce to 3-sat

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        try{
            String line = br.readLine();
            while(line != null){
                interpretLine(line);
                line = br.readLine();
            }
            br.close();
        }
        catch(IOException e){
            System.out.println("Read error");
            System.exit(-1);
        }

       /* for(String clause : satClauses){
            System.out.println(clause + " 0");
        }*/

        if(satClauses.size() != numSatClauses){
            System.out.println("sat clauses dont add to num given");
            System.out.println(clauseCount);
            System.out.println(satClauses.size());
            System.exit(-1);
        }

        int newLit = numSatVars + 1;
        boolean firstClause = true;
        int numSat3vars = numSatVars;


        ArrayList<String> sat3clauses = new ArrayList<String>();
        //begin alg to convert sat to 3-sat
        for(int a = 0; a < satClauses.size(); a++){
            if(!satClauses.get(a).equals("")){
                String[] lits = satClauses.get(a).split(" ");
                if(lits.length > 3) {
                    int numClauses = lits.length - 2;

                    int ptr = 0;
                    for(int x = 0; x < numClauses; x++){
                        if(x == 0){
                            if(firstClause){
                                sat3clauses.add(lits[ptr] + " " + lits[ptr+1] + " " + newLit);
                                numSat3vars++;
                                firstClause = false;
                            }
                            else{
                                if (newLit < 0) {
                                    newLit = newLit * (-1);
                                    newLit = newLit + 1;
                                    numSat3vars++;
                                } else if (newLit > 0) {
                                    newLit = newLit * (-1);
                                }
                                sat3clauses.add(lits[ptr] + " " + lits[ptr+1] + " " + newLit);

                            }
                            ptr += 2;
                        }
                        else if( x == numClauses-1){
                            if (newLit < 0) {
                                newLit = newLit * (-1);
                                newLit = newLit + 1;
                                numSat3vars++;
                            } else if (newLit > 0) {
                                newLit = newLit * (-1);
                            }
                            sat3clauses.add(newLit + " " + lits[ptr] + " " + lits[ptr+1]);

                        }
                        else{
                            if (newLit < 0) {
                                newLit = newLit * (-1);
                                newLit = newLit + 1;
                                numSat3vars++;
                            } else if (newLit > 0) {
                                newLit = newLit * (-1);
                            }
                            sat3clauses.add(newLit + " " + lits[ptr] + " ");

                            if (newLit < 0) {
                                newLit = newLit * (-1);
                                newLit = newLit + 1;
                                numSat3vars++;
                            } else if (newLit > 0) {
                                newLit = newLit * (-1);
                            }
                            sat3clauses.set((sat3clauses.size()-1), sat3clauses.get((sat3clauses.size()-1)) + newLit);

                            ptr++;
                        }
                    }
                }
                else{
                    for(int b = 0; b < lits.length; b++){
                        if(b == 0){
                            sat3clauses.add(lits[b]);
                        }
                        else{
                            sat3clauses.set((sat3clauses.size()-1), sat3clauses.get((sat3clauses.size()-1)) + " " + lits[b]);
                        }
                    }

                }
            }
            else{
                sat3clauses.add("");
            }

        }

        System.out.println("c 3-sat return");
        System.out.println("p cnf " + numSat3vars + " " + sat3clauses.size());
        for(String clause : sat3clauses){
            System.out.println(clause + " 0");
        }

        System.exit(0);


    }
    public static void interpretLine(String line){
        String[] components = line.split(" ");
        //Interprets a line from DIMACS CNF input
        //Turns instance into ArrayList data structure with " " to divide each literal of a clause
        if(components[0].equals("c")){
            if(!commentPhase){
                System.out.println("no comments after p line");
                System.exit(-1);
            }
        }
        else if(components[0].equals("p")){
            commentPhase = false;
            problemPhase = true;
            if(clausePhase){
                System.out.println("multiple p lines detected");
                System.exit(-1);
            }
            if(!components[1].contains("cnf")){
                System.out.println("not cnf");
                System.exit(-1);
            }
            else{
                numSatVars = Integer.parseInt(components[2]);
                numSatClauses = Integer.parseInt(components[3]);
                //satClauses = new ArrayList<String>(numSatClauses);
            }
            clausePhase = true;
        }
        else{
            if(!problemPhase && !line.isEmpty()){
                System.out.println("P line not reached");
                System.exit(-1);
            }
            if(!line.isEmpty()){
                clausePhase = true;
                for(int c = 0; c < components.length; c++){

                    int currLit = Integer.parseInt(components[c]);
                    currLit = Math.abs(currLit);
                    if(currLit > numSatVars){
                        System.out.println("variable out of range");
                        System.exit(-1);
                    }
                    if(newClause){
                        if(!components[c].equals("0")){
                            if(!line.isEmpty()){

                                clauseCount++;
                                satClauses.add(components[c]);
                                newClause = false;
                            }

                        }
                        else{
                            if(satClauses.size()==0){
                                clauseCount++;
                                satClauses.add("");
                            }
                        }
                    }
                    else{
                        if(components[c].equals("0")){
                            newClause = true;
                            if(satClauses.size()==0){
                                //clauseCount++;
                                satClauses.add("");
                            }
                        }
                        else{
                            satClauses.set((satClauses.size()-1), satClauses.get(satClauses.size()-1) + " " + components[c]);
                            newClause = false;
                        }
                    }
                }
            }


        }


    }
}