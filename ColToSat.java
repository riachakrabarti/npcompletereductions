import java.util.*;
import java.io.BufferedReader;
import java.io.*;

public class ColToSat {

    public static int numNodes = 0;
    public static int numEdges = 0;
    public static int numColours = 0;
    public static boolean commentPhase = true;
    public static boolean problemPhase = false;
    public static boolean colourPhase = false;
    public static ArrayList<String> kColEdges = new ArrayList<String>();
    public static ArrayList<String> kColNodes = new ArrayList<String>();


    public static void main(String[] args){
        coltosat();
    }

    public static void coltosat(){
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

        if(kColEdges.size() != numEdges){
            System.out.println("Not enough/too many edges");
            System.exit(-1);
        }

        int numLiterals = numNodes * numColours; //each node could possibly be one colour. Each integer from 1-numLiterals represents a node/colour combo

        ArrayList<String> satClauses = new ArrayList<String>();
        //These add all the possible colours of a single node in one clause (ALO clauses)
        int ptr = 1;
        while(ptr < (numLiterals+1)){
            for(int x = 0; x < numColours; x++){
                if(x == 0){
                    satClauses.add(ptr + "");
                    ptr++;
                }
                else{
                    satClauses.set((satClauses.size()-1), satClauses.get(satClauses.size()-1) + " " + ptr);
                    ptr++;
                }
            }
        }

        //AMO clauses, nots for every other combo of colour and node
        ptr = 1;
        for(int z = 1; z < numColours-1; z++){
            for(int n = 0; n< numColours; n++){
                boolean added = false;
                for(int c = 0; c < numColours; c++){
                    if((ptr + n) != (ptr + c)){
                        if(!added){
                            satClauses.add(((ptr+c) * -1) + "");
                            added = true;
                        }
                        else{
                            satClauses.set((satClauses.size()-1), satClauses.get(satClauses.size()-1) + " " + ((ptr+c) * -1));
                        }
                    }
                }
                
            }
            ptr += numColours;
        }
        

        //Edge clauses for making sure two connecting nodes dont have the same colour

        for(String edgeClause : kColEdges){
            String[] edgeParts = edgeClause.split(" ");
            int v = Integer.parseInt(edgeParts[1]);
            int w = Integer.parseInt(edgeParts[2]);
            int ptr1 = 1;
            int ptr2 = 1;
            for(int p = 1; p < v; p++){
                ptr1 += numColours;
            }
            for(int q = 1; q < w; q++){
                ptr2 += numColours;
            }
            for(int b = 0; b < numColours; b++){
                int lit1 = ptr1 * -1;
                int lit2 = ptr2 * -1;
                satClauses.add(lit1 + " " + lit2);
                ptr1++;
                ptr2++;
            }
        }

        System.out.println("c col to sat return");
        System.out.println("p cnf " + (numLiterals+1) + " " + satClauses.size());
        for(String clause : satClauses){
            System.out.println(clause + " 0");
        }

        System.exit(0);


    }

    public static void interpretLine(String line){
        String[] components = line.split(" ");

        if(components[0].equals("c")){
            if(!commentPhase){
                System.out.println("no comments after p line");
                System.exit(-1);
            }
        }
        else if(components[0].equals("p")){
            commentPhase = false;
            if(problemPhase){
                System.out.println("multiple p lines detected");
                System.exit(-1);
            }
            problemPhase = true;
            if(!components[1].contains("edge")){
                System.out.println("not col problem");
                System.exit(-1);
            }
            else{
                numNodes = Integer.parseInt(components[2]);
                numEdges = Integer.parseInt(components[3]);
            }
        }
        else if(components[0].equals("colours")){
            colourPhase = true;
            if(!problemPhase){
                System.out.println("p line not reached");
                System.exit(-1);
            }
            numColours = Integer.parseInt(components[1]);
        }
        else if(components[0].equals("e")){
            if(!problemPhase){
                System.out.println("p line not reached");
                System.exit(-1);
            }
            if(!colourPhase){
                System.out.println("colour line not reached");
                System.exit(-1);
            }
            int a = Integer.parseInt(components[1]);
            int b = Integer.parseInt(components[2]);
            if(a < 0 || b < 0){
                System.out.println("no negative numbers in edges");
                System.exit(-1);
            }
            checkDuplicates(a,b);
            kColEdges.add(line);
        }
    }
    public static boolean checkDuplicates(int a, int b){
        for(String line : kColEdges){
            String[] checks = line.split(" ");
            int x = Integer.parseInt(checks[1]);
            int y = Integer.parseInt(checks[2]);
            if(a == x){
                if(b == y){
                    System.out.println("Duplicate clause");
                    System.exit(-1);
                }
            }
            if(b == x){
                if (a == y){
                    System.out.println("Duplicate clause");
                    System.exit(-1);
                }
            }

        }
        return true;
    }

}