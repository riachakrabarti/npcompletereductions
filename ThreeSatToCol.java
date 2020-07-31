import java.util.*;
import java.io.BufferedReader;
import java.io.*;

public class ThreeSatToCol {

    public static int numSatVars = 0;
    public static int numSatClauses = 0;
    public static int clauseCount = 0;
    public static ArrayList<String> satClauses = new ArrayList<String>();
    public static int clauseBuilderSat = 0;
    public static boolean newClause = true;
    public static boolean commentPhase = true;
    public static boolean problemPhase = false;
    public static boolean clausePhase = false;
    public static ArrayList<String> edgeClauses = new ArrayList<String>();


    public static void main(String[] args){
        threesattocol();
    }

    public static void threesattocol(){
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

        //make it 4 variables if n < 4
        if(numSatVars < 4){
            numSatVars = 4;
        }

        //number of nodes that are positive
        int numColNodes = (numSatVars * 3) + numSatClauses;

        //number of colours = n+1
        int numColours = numSatVars +1;

        //connect x and neg x nodes
        for(int a = 1; a < numSatVars+1; a++){
            int x1 = a;
            int x1neg = a+numSatVars;
            edgeClauses.add("e " + x1 + " " + x1neg);
        }

        //connect y nodes
        int ystart = (numSatVars*2)+1;
        for(int b = 0; b < numSatVars; b++){
            for(int c = 0; c < numSatVars; c++){
                if((ystart+b) != (ystart+c)){
                    if(checkDuplicates((ystart+b), (ystart+c))){
                        edgeClauses.add("e " + (ystart+b) + " " + (ystart+c));
                    }
                }
            }

        }

        //ys and xs and negxs
        int currX = 1;

        for(int d = 0; d < numSatVars; d++){
            for(int e = 0; e < numSatVars; e++){
                if((ystart+e) != (currX+numSatVars)){
                    int negX = currX +numSatVars;
                    if(checkDuplicates((ystart+e), negX)){
                        edgeClauses.add("e " + (ystart+e) + " " + negX);
                    }
                    if(checkDuplicates((ystart+e), currX)){
                        edgeClauses.add("e " + (ystart+e) + " " + currX);
                    }
                }
            }
            currX++;
        }

        //connect clauses to variables not in those clauses
        currX = 1;
        int currClause = numColNodes - numSatClauses +1;
        for(String clause : satClauses){

            for(int f = 0; f < numSatVars; f++){
                int negX = (currX+f)+numSatVars;
                if(!inLine(clause,(currX+f))){
                    if(checkDuplicates(currClause, (currX+f))){
                        edgeClauses.add("e " + (currClause) + " " + (currX+f));
                    }

                }
                if(!inLine(clause, negX)){
                    if(checkDuplicates(currClause, negX)){
                        edgeClauses.add("e " + currClause + " " + negX);
                    }
                }
            }
            currClause++;

        }

        System.out.println("c Three sat to kCol");
        int fullColNodes = (numSatVars * 3) + numSatClauses;
        System.out.println("p edge "+ fullColNodes + " " + edgeClauses.size());
        System.out.println("colours " + numColours);

        for(String clause : edgeClauses){
            System.out.println(clause);
        }

        System.exit(0);




    }

    public static boolean inLine(String line, int a){
        String[] clauseLits = line.split(" ");
        for(String part : clauseLits){
            int p = Integer.parseInt(part);
            if(p == a){
                return true;
            }
        }
        return false;
    }

    public static boolean checkDuplicates(int a, int b){
        for(String line : edgeClauses){
            String[] checks = line.split(" ");
            int x = Integer.parseInt(checks[1]);
            int y = Integer.parseInt(checks[2]);
            if(a == x){
                if(b == y){
                    //System.out.println("Duplicate clause");
                    return false;
                }
            }
            if(b == x){
                if (a == y){
                    //System.out.println("Duplicate clause");
                    return false;
                }
            }

        }
        return true;
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
            if(components.length > 4){
                System.out.println("not 3-sat");
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
                    }
                    else{
                        if(components[c].equals("0")){
                            newClause = true;
                            if(satClauses.size()==0){
                                clauseCount++;
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