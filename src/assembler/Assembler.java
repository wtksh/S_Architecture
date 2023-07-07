package assembler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

import org.hamcrest.core.IsNull;

import components.Register;

import architecture.Architecture1;

public class Assembler {
	
	private ArrayList<String> lines;
	private ArrayList<String> objProgram;
	private ArrayList<String> execProgram;
	private Architecture1 arch;
	private ArrayList<String>commands;	
	private ArrayList<String>labels;
	private ArrayList<Integer> labelsAdresses;
	private ArrayList<String>variables;
	
	
	public Assembler() {
		lines = new ArrayList<>();
		labels = new ArrayList<>();
		labelsAdresses = new ArrayList<>();
		variables = new ArrayList<>();
		objProgram = new ArrayList<>();
		execProgram = new ArrayList<>();
		arch = new Architecture1();
		commands = arch.getCommandsList();	
	}
	
	//getters
	
	public ArrayList<String> getObjProgram() {
		return objProgram;
	}
	
	/**
	 * These methods getters and set below are used only for TDD purposes
	 * @param lines
	 */
	
	protected ArrayList<String> getLabels() {
		return labels;
	}
	
	protected ArrayList<Integer> getLabelsAddresses() {
		return labelsAdresses;
	}
	
	protected ArrayList<String> getVariables() {
		return variables;
	}
	
	protected ArrayList<String> getExecProgram() {
		return execProgram;
	}
	
	protected void setLines(ArrayList<String> lines) {
		this.lines = lines;
	}	

	protected void setExecProgram(ArrayList<String> lines) {
		this.execProgram = lines;
	}	
	
	
	/*
	 * An assembly program is always in the following template
	 * <variables>
	 * <commands>
	 * Obs.
	 * 		variables names are always started with alphabetical char
	 * 	 	variables names must contains only alphabetical and numerical chars
	 *      variables names never uses any command name
	 * 		names ended with ":" identifies labels i.e. address in the memory
	 * 		Commands are only that ones known in the architecture. No comments allowed
	 * 	
	 * 		The assembly file must have the extention .dsf
	 * 		The executable file must have the extention .dxf 	
	 */
	


	/**
	 * This method reads an entire file in assembly 
	 * @param filename
	 * @throws IOException 
	 */
	public void read(String filename) throws IOException {
		   BufferedReader br = new BufferedReader(new		 
		   FileReader(filename+".dsf"));
		   String linha;
		   while ((linha = br.readLine()) != null) {
			     lines.add(linha);
			}
			br.close();
			
	}
	

	/**
	 * This method scans the strings in lines
	 * generating, for each one, the corresponding machine code
	 * @param lines
	 */
	public void parse() {
		for (String s:lines) {
			String tokens[] = s.split(" ");
			if (findCommandNumber(tokens)>=0) { //the line is a command
				processCommand(tokens);
			}
			else { //the line is not a command: so, it can be a variable or a label
				if (tokens[0].endsWith(":")){ //if it ends with : it is a label
					String label = tokens[0].substring(0, tokens[0].length()-1); //removing the last character
					labels.add(label);
					labelsAdresses.add(objProgram.size());
				}
				else //otherwise, it must be a variable
					variables.add(tokens[0]);
			}
		}
		
	}



	/**
	 * This method processes a command, putting it and its parameters (if they have)
	 * into the final array
	 * @param tokens
	 */
	protected void processCommand(String[] tokens) {
		String command = tokens[0];
		String parameter ="";
		String parameter2 = "";
		String parameter3 = "";
		int commandNumber = findCommandNumber(tokens);
		if (commandNumber == 0) { //must to process an addRegReg command
			parameter = tokens[1];
			parameter2 = tokens[2];
		}
		if (commandNumber == 1) { //must to process an addMemReg command
			parameter = tokens[1];
			parameter2 = tokens[2];
			parameter = "&" + parameter; //this is a flag to indicate that is position in memory
		}
		if (commandNumber == 2) { //must to process an addRegMem command
			parameter = tokens[1];
			parameter2 = tokens[2];
			parameter2 = "&" + parameter2; //this is a flag to indicate that is position in memory
		}
		if (commandNumber == 3) { //must to process a subRegReg command
			parameter = tokens[1];
			parameter2 = tokens[2];
		}
		if (commandNumber == 4) { //must to process a subMemReg command
			parameter = tokens[1];
			parameter2 = tokens[2];
			parameter = "&" + parameter; //this is a flag to indicate that is position in memory
		}
		if (commandNumber == 5) { //must to process a subRegMem command
			parameter = tokens[1];
			parameter2 = tokens[2];
			parameter2 = "&" + parameter2; //this is a flag to indicate that is position in memory
		}
		if (commandNumber == 6) { //must to process an imulMemReg command
			parameter = tokens[1];
			parameter2 = tokens[2];
			parameter = "&" + parameter; //this is a flag to indicate that is position in memory
		}
		if (commandNumber == 7) { //must to process an imulRegMem command
			parameter = tokens[1];
			parameter2 = tokens[2];
			parameter2 = "&" + parameter2; //this is a flag to indicate that is position in memory
		}
		if (commandNumber == 8) { //must to process an imulRegReg command
			parameter = tokens[1];
			parameter2 = tokens[2];
		}
		if (commandNumber == 9) { //must to process a moveMemReg command
			parameter = tokens[1];
			parameter2 = tokens[2];
			parameter = "&" + parameter; //this is a flag to indicate that is position in memory
		}
		if (commandNumber == 10) { //must to process a moveRegMem command
			parameter = tokens[1];
			parameter2 = tokens[2];
			parameter2 = "&" + parameter2; //this is a flag to indicate that is position in memory
		}
		if (commandNumber == 11) { //must to process a moveRegReg command
			parameter = tokens[1];
			parameter2 = tokens[2];
		}
		if (commandNumber == 12) { //must to process a moveImmReg command
			parameter = tokens[1];
			parameter2 = tokens[2];
		}
		if (commandNumber == 13) { //must to process an incReg command
			parameter = tokens[1];
		}
		if (commandNumber == 14) { //must to process an incMem command
			parameter = tokens[1];
			parameter = "&" + parameter; //this is a flag to indicate that is position in memory
		}
		if (commandNumber == 15) { //must to process a jmp command
			parameter = tokens[1];
			parameter = "&" + parameter; //this is a flag to indicate that is position in memory
		}
		if (commandNumber == 16) { //must to process a jn command
			parameter = tokens[1];
			parameter = "&" + parameter; //this is a flag to indicate that is position in memory
		}
		if (commandNumber == 17) { //must to process a jz command
			parameter = tokens[1];
			parameter = "&" + parameter; //this is a flag to indicate that is position in memory
		}
		if (commandNumber == 18) { //must to process a jnz command
			parameter = tokens[1];
			parameter = "&" + parameter; //this is a flag to indicate that is position in memory
		}
		if (commandNumber == 19) { //must to process a jeq command
			parameter = tokens[1];
			parameter2 = tokens[2];
			parameter3 = tokens[3];
			parameter3 = "&" + parameter3; //this is a flag to indicate that is position in memory
		}
		if (commandNumber == 20) { //must to process a jgt command
			parameter = tokens[1];
			parameter2 = tokens[2];
			parameter3 = tokens[3];
			parameter3 = "&" + parameter3; //this is a flag to indicate that is position in memory
		}
		if (commandNumber == 21) { //must to process a jlw command
			parameter = tokens[1];
			parameter2 = tokens[2];
			parameter3 = tokens[3];
			parameter3 = "&" + parameter3; //this is a flag to indicate that is position in memory
		}

		objProgram.add(Integer.toString(commandNumber));
		if (!parameter.isEmpty()) {
			objProgram.add(parameter);
		}
		if (!parameter2.isEmpty()) {
			objProgram.add(parameter2);
		}
		if (!parameter3.isEmpty()) {
			objProgram.add(parameter3);
		}
	}
	

	/**
	 * This method uses the tokens to search a command
	 * in the commands list and returns its id.
	 * Some commands (as move) can have multiple formats (reg reg, mem reg, reg mem) and
	 * multiple ids, one for each format.
	 * @param tokens
	 * @return
	 */
	private int findCommandNumber(String[] tokens) {
		int p = commands.indexOf(tokens[0]);
		
		if (p<0){ //the command isn't in the list. So it must have multiple formats
			if ("add".equals(tokens[0])) //the command is an add
				p = processAdd(tokens);
			
			if ("sub".equals(tokens[0])) //the command is a sub
				p = processSub(tokens);

			if ("imul".equals(tokens[0])) //the commands is a imul
				p = processImul(tokens);

			if ("move".equals(tokens[0])) //the command is a move
				p = processMove(tokens);

			if ("inc".equals(tokens[0])) //the command is an inc
				p = processInc(tokens);
		}

		return p;
	}

	/**
	 * This method process a add command.
	 * It must have different formats, meaning different internal commands
	 * @param tokens
	 * @return
	 */
	private int processAdd(String[] tokens) {
		String p1 = tokens[1];
		String p2 = tokens[2];
		int p=-1;
		if ((p1.startsWith("%"))&&(p2.startsWith("%"))) { //this is a addRegReg command
			p = commands.indexOf("addRegReg");
		}
		else if ((p1.startsWith("%"))) { //this is a addRegMem command
			p = commands.indexOf("addRegMem");
		}
		else if ((p2.startsWith("%"))) { //this is a addMemReg command
			p = commands.indexOf("addMemReg");
		}
		return p;
	}

	/**
	 * This method process a sub command.
	 * It must have different formats, meaning different internal commands
	 * @param tokens
	 * @return
	 */
	private int processSub(String[] tokens) {
		String p1 = tokens[1];
		String p2 = tokens[2];
		int p=-1;
		if ((p1.startsWith("%"))&&(p2.startsWith("%"))) { //this is a subRegReg command
			p = commands.indexOf("subRegReg");
		}
		else if ((p1.startsWith("%"))) { //this is a subRegMem command
			p = commands.indexOf("subRegMem"); 
		}
		else if ((p2.startsWith("%"))) { //this is a subMemReg command
			p = commands.indexOf("subMemReg");
		}
		return p;
	}

	/**
	 * This method process a imul command.
	 * It must have different formats, meaning different internal commands
	 * @param tokens
	 * @return
	 */
	private int processImul(String[] tokens) {
		String p1 = tokens[1];
		String p2 = tokens[2];
		int p=-1;
		if ((p1.startsWith("%"))&&(p2.startsWith("%"))) { //this is a imulRegReg command
			p = commands.indexOf("imulRegReg");
		}
		else if ((p1.startsWith("%"))) { //this is a imulRegMem command
			p = commands.indexOf("imulRegMem");
		}
		else if ((p2.startsWith("%"))) { //this is a imulMemReg command
			p = commands.indexOf("imulMemReg");
		}
		return p;
	}

	/**
	 * This method process a move command.
	 * It must have different formats, meaning different internal commands
	 * @param tokens
	 * @return
	 */
	private int processMove(String[] tokens) {
		String p1 = tokens[1];
		String p2 = tokens[2];
		int p=-1;
		if ((p1.startsWith("%"))&&(p2.startsWith("%"))) { //this is a moveRegReg command
			p = commands.indexOf("moveRegReg");
		}
		else if ((p1.startsWith("%"))) { //this is a moveRegMem command
			p = commands.indexOf("moveRegMem");
		}
		else if ((p2.startsWith("%"))) { //this is a moveMemReg or moveImmReg command
			if (p1.matches("-?[0-9]+")) { // this is a moveImmReg
				p = commands.indexOf("moveImmReg");
			}
			else { // this is a moveMemReg
				p = commands.indexOf("moveMemReg");
			}		
		}
		return p;
	}


	/**
	 * This method process a move command.
	 * It must have different formats, meaning different internal commands
	 * @param tokens
	 * @return
	 */
	private int processInc(String[] tokens) {
		String p1 = tokens[1];
		int p=-1;
		if ((p1.startsWith("%"))) { //this is a incReg command
			p = commands.indexOf("incReg");
		}
		else { //this is a incMem command
			p = commands.indexOf("incMem");
		}
		return p;
	}

	/**
	 * This method creates the executable program from the object program
	 * Step 1: check if all variables and labels mentioned in the object 
	 * program are declared in the source program
	 * Step 2: allocate memory addresses (space), from the end to the begin (stack)
	 * to store variables
	 * Step 3: identify memory positions to the labels
	 * Step 4: make the executable by replacing the labels and the variables by the
	 * corresponding memory addresses 
	 * @param filename 
	 * @throws IOException 
	 */
	public void makeExecutable(String filename) throws IOException {
		if (!checkLabels())
			return;
		execProgram = (ArrayList<String>) objProgram.clone();
		replaceAllVariables();
		replaceLabels(); //replacing all labels by the address they refer to
		replaceRegisters(); //replacing all registers by the register id they refer to
		saveExecFile(filename);
		System.out.println("Finished");
	}

	/**
	 * This method replaces all the registers names by its correspondings ids.
	 * registers names must be prefixed by %
	 */
	protected void replaceRegisters() {
		int p=0;
		for (String line:execProgram) {
			if (line.startsWith("%")){ //this line is a register
				line = line.substring(1, line.length());
				int regId = searchRegisterId(line, arch.getRegistersList());
				String newLine = Integer.toString(regId);
				execProgram.set(p, newLine);
			}
			p++;
		}
		
	}

	/**
	 * This method replaces all variables by their addresses.
	 * The addresses o0f the variables startes in the end of the memory
	 * and decreases (creating a stack)
	 */
	protected void replaceAllVariables() {
		int position = arch.getMemorySize()-1; //starting from the end of the memory
		for (String var : this.variables) { //scanning all variables
			replaceVariable(var, position);
			position --;
		}
	}

	/**
	 * This method saves the execFile collection into the output file
	 * @param filename
	 * @throws IOException 
	 */
	private void saveExecFile(String filename) throws IOException {
		File file = new File(filename+".dxf");
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		for (String l : execProgram)
			writer.write(l+"\n");
		writer.write("-1"); //-1 is a flag indicating that the program is finished
		writer.close();
		
	}

	/**
	 * This method replaces all labels in the execprogram by the corresponding
	 * address they refer to
	 */
	protected void replaceLabels() {
		int i=0;
		for (String label : labels) { //searching all labels
			label = "&"+label;
			int labelPointTo = labelsAdresses.get(i);
			int lineNumber = 0;
			for (String l : execProgram) {
				if (l.equals(label)) {//this label must be replaced by the address
					String newLine = Integer.toString(labelPointTo); // the address
					execProgram.set(lineNumber, newLine);
				}
				lineNumber++;
			}
			i++;
		}
		
	}

	/**
	 * This method replaces all occurences of a variable
	 * name found in the object program by his address
	 * in the executable program
	 * @param var
	 * @param position
	 */
	protected void replaceVariable(String var, int position) {
		var = "&"+var;
		int i=0;
		for (String s:execProgram) {
			if (s.equals(var)) {
				s = Integer.toString(position);
				execProgram.set(i, s);
			}
			i++;
		}
	}

	/**
	 * This method checks if all labels and variables in the object program were in the source
	 * program.
	 * The labels and the variables collection are used for this
	 */
	protected boolean checkLabels() {
		System.out.println("Checking labels and variables");
		for (String line:objProgram) {
			boolean found = false;
			if (line.startsWith("&")) { //if starts with "&", it is a label or a variable
				line = line.substring(1, line.length());
				if (labels.contains(line))
					found = true;
				if (variables.contains(line))
					found = true;
				if (!found) {
					System.out.println("FATAL ERROR! Variable or label "+line+" not declared!");
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * This method searches for a register in the architecture register list
	 * by the register name
	 * @param line
	 * @param registersList
	 * @return
	 */
	private int searchRegisterId(String line, ArrayList<Register> registersList) {
		int i=0;
		for (Register r:registersList) {
			if (line.equals(r.getRegisterName())) {
				return i;
			}
			i++;
		}
		return -1;
	}

	public static void main(String[] args) throws IOException {
		String filename = "program";
		Assembler assembler = new Assembler();
		System.out.println("Reading source assembler file: "+filename+".dsf");
		assembler.read(filename);
		System.out.println("Generating the object program");
		assembler.parse();
		System.out.println("Generating executable: "+filename+".dxf");
		assembler.makeExecutable(filename);
	}
		
}
