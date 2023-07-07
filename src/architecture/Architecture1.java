package architecture;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import components.Bus;
import components.Memory;
import components.Register;
import components.Ula;

public class Architecture1 {
	
	private boolean simulation; //this boolean indicates if the execution is done in simulation mode.
								//simulation mode shows the components' status after each instruction
	
	
	private boolean halt;
	private Bus extbus1;
	private Bus intbus1;
	private Memory memory;
	private int memorySize;
	private Register PC;
	private Register IR;
	private Register RPG0;
	private Register RPG1;
	private Register RPG2;
	private Register RPG3;
	private Register Flags;
	private Ula ula;
	private Bus demux; //only for multiple register purposes
	
	private ArrayList<String> commandsList;
	private ArrayList<Register> registersList;
	
	

	/**
	 * Instanciates all components in this architecture
	 */
	private void componentsInstances() {
		//don't forget the instantiation order
		//buses -> registers -> ula -> memory
		extbus1 = new Bus();
		intbus1 = new Bus();

		PC = new Register("PC", extbus1, extbus1);
		IR = new Register("IR", extbus1, extbus1);
		RPG0 = new Register("RPG0", extbus1, intbus1);
		RPG1 = new Register ("RPG1", extbus1, intbus1);
		RPG2 = new Register("RPG2", extbus1, intbus1);
		RPG3 = new Register ("RPG3", extbus1, intbus1);
		Flags = new Register(2, intbus1);
		fillRegistersList();

		ula = new Ula(intbus1, intbus1);
		memorySize = 128;
		memory = new Memory(memorySize, extbus1);
		demux = new Bus(); //this bus is used only for multiple register operations
		
		fillCommandsList();
	}

	/**
	 * This method fills the registers list inserting into them all the registers we have.
	 * IMPORTANT!
	 * The first register to be inserted must be the default RPG
	 */
	private void fillRegistersList() {
		registersList = new ArrayList<Register>();
		registersList.add(RPG0);
		registersList.add(RPG1);
		registersList.add(RPG2);
		registersList.add(RPG3);
		registersList.add(PC);
		registersList.add(IR);
		registersList.add(Flags);
	}

	/**
	 * Constructor that instanciates all components according the architecture diagram
	 */
	public Architecture1() {
		componentsInstances();
		
		//by default, the execution method is never simulation mode
		simulation = false;
	}

	
	public Architecture1(boolean sim) {
		componentsInstances();
		
		//in this constructor we can set the simoualtion mode on or off
		simulation = sim;
	}



	//getters
	
	//Um Para Cada Barramento
	protected Bus getExtbus1() {
		return extbus1;
	}

	protected Bus getIntbus1() {
		return intbus1;
	}

	protected Memory getMemory() {
		return memory;
	}

	protected Register getPC() {
		return PC;
	}

	protected Register getIR() {
		return IR;
	}

	//Um Para Cada Resgistrador RPG
	protected Register getRPG0() {
		return RPG0;
	}
	
	protected Register getRPG1() {
		return RPG1;
	}
	
	protected Register getRPG2() {
		return RPG2;
	}
	
	protected Register getRPG3() {
		return RPG3;
	}

	protected Register getFlags() {
		return Flags;
	}

	protected Ula getUla() {
		return ula;
	}

	public ArrayList<String> getCommandsList() {
		return commandsList;
	}



	//all the microprograms must be implemented here
	//the instructions table is
	/*
	 * add %<regA> %<regB>    || RegB <- RegA + RegB
	 * add <mem> %<regA>      || RegA <- memória[mem] + RegA
	 * add %<regA> <mem>      || Memória[mem] <- RegA + memória[mem]
	 * sub <regA> <regB>      || RegB <- RegA - RegB
	 * sub <mem> %<regA>      || RegA <- memória[mem] - RegA
	 * sub %<regA> <mem>      || memória[mem] <- RegA - memória[mem]
	 * imul <mem> %<RegA>     || RegA <- RegA x memória[mem] (produto de inteiros)
	 * imul %<RegA> <mem>     || memória[mem] <- RegA x memória[mem] (idem)
	 * imul %<RegA> <RegB>    || RegB <- RegA x RegB (idem)
	 * move <mem> %<regA>     || RegA <- memória[mem]
	 * move %<regA> <mem>     || memória[mem] <- RegA
	 * move %<regA> %<regB>   || RegB <- RegA
	 * move imm %<regA>       || RegA <- immediate
	 * inc %<regA>            || RegA ++
	 * inc <mem>              || memória[mem] ++
	 * jmp <mem>              || PC <- mem (desvio incondicional)
	 * jn <mem>               || se última operação<0 então PC <- mem (desvio condicional)
	 * jz <mem>               || se última operação=0 então PC <- mem (desvio condicional)
	 * jnz <mem>              || se última operação|=0 então PC <- mem (desvio condicional)
	 * jeq %<regA> %<regB> <mem>   || se RegA==RegB então PC <- mem (desvio condicional)
	 * jgt %<regA> %<regB> <mem>   || se RegA>RegB então PC <- mem (desvio condicional)
	 * jlw %<regA> %<regB> <mem>   || se RegA<RegB então PC <- mem (desvio condicional)
	 */
	
	/**
	 * This method fills the commands list arraylist with all commands used in this architecture
	 */
	protected void fillCommandsList() {
		commandsList = new ArrayList<String>();
		//Adds
		commandsList.add("addRegReg");   //0
		commandsList.add("addMemReg");   //1
		commandsList.add("addRegMem");   //2
		
		//Subs
		commandsList.add("subRegReg");   //3
		commandsList.add("subMemReg");   //4
		commandsList.add("subRegMem");   //5
		
		//Imuls
		commandsList.add("imulMemReg");	//6
		commandsList.add("imulRegMem");	//7
		commandsList.add("imulRegReg");	//8

		// Moves
		commandsList.add("moveMemReg");	//9
		commandsList.add("moveRegMem");	//10
		commandsList.add("moveRegReg");	//11
		commandsList.add("moveImmReg");	//12

		// Incs
		commandsList.add("incReg");	//13
		commandsList.add("incMem");	//14

		//Desvios
		commandsList.add("jmp");	//15
		commandsList.add("jn");	//16
		commandsList.add("jz");	//17
		commandsList.add("jnz");	//18
		commandsList.add("jeq");	//19
		commandsList.add("jg");	//20
		commandsList.add("jlw");	//21
	}

	
	/**
	 * This method is used after some ULA operations, setting the flags bits according the result.
	 * @param result is the result of the operation
	 * NOT TESTED!!!!!!!
	 */
	private void setStatusFlags(int result) {
		Flags.setBit(0, 0);
		Flags.setBit(1, 0);
		if (result==0) { //bit 0 in flags must be 1 in this case
			Flags.setBit(0,1);
		}
		if (result<0) { //bit 1 in flags must be 1 in this case
			Flags.setBit(1,1);
		}
	}

	/**
	 * This method implements the microprogram for
	 * 					ADD address
	 * In the machine language this command number is 0, and the address is in the position next to him
	 *    
	 * where address is a valid position in this memory architecture
	 * The method reads the value from memory (position address) and 
	 * performs an add with this value and that one stored in the RPG (the first register in the register list).
	 * The final result must be in RPG (the first register in the register list).
	 * @param address
	 */
	public void addRegReg() {
		//PC++
		RPG0.read();
		IR.store();//Save original value RPG0
		PC.read();
		RPG0.store();
		RPG0.internalRead();
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		RPG0.internalStore();
		RPG0.read();
		PC.store();//now PC points to the parameter address
		IR.read();
		RPG0.store();//Back original value for RPG0
		PC.read();//Return pc value for extbus1
		
		//Get <Reg1>
		memory.read();
		demux.put(extbus1.get());
		registersInternalRead();
		ula.internalStore(0);

		//PC++
		RPG0.read();
		IR.store();//Save original value RPG0
		PC.read();
		RPG0.store();
		RPG0.internalRead();
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		RPG0.internalStore();
		RPG0.read();
		PC.store();//now PC points to the parameter address
		IR.read();
		RPG0.store();//Back original value for RPG0
		PC.read();//Return pc value for extbus1

		//Get <Reg2>
		memory.read();
		demux.put(extbus1.get());
		registersInternalRead();
		ula.internalStore(1);

		//ADD
		ula.add();
		ula.internalRead(1);
		setStatusFlags(intbus1.get());
		registersInternalStore();
		
		//PC++
		RPG0.read();
		IR.store();//Save original value RPG0
		PC.read();
		RPG0.store();
		RPG0.internalRead();
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		RPG0.internalStore();
		RPG0.read();
		PC.store();//now PC points to the parameter address
		IR.read();
		RPG0.store();//Back original value for RPG0
		PC.read();//Return pc value for extbus1

	}

	public void addMemReg(){
		//PC++
		RPG0.read();
		IR.store();//Save original value RPG0
		PC.read();
		RPG0.store();
		RPG0.internalRead();
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		RPG0.internalStore();
		RPG0.read();
		PC.store();//now PC points to the parameter address
		IR.read();
		RPG0.store();//Back original value for RPG0
		PC.read();//Return pc value for extbus1

		//Get Mem
		RPG0.read();
		IR.store();
		PC.read();
		memory.read();
		memory.read();
		RPG0.store();
		RPG0.internalRead();
		ula.internalStore(0);
		IR.read();
		RPG0.store();

		//PC++
		RPG0.read();
		IR.store();//Save original value RPG0
		PC.read();
		RPG0.store();
		RPG0.internalRead();
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		RPG0.internalStore();
		RPG0.read();
		PC.store();//now PC points to the parameter address
		IR.read();
		RPG0.store();//Back original value for RPG0
		PC.read();//Return pc value for extbus1

		//Get Reg
		memory.read();
		demux.put(extbus1.get());
		registersInternalRead();
		ula.internalStore(1);

		//Add
		ula.add();
		ula.internalRead(1);
		setStatusFlags(intbus1.get());
		registersInternalStore();

		//PC++
		RPG0.read();
		IR.store();//Save original value RPG0
		PC.read();
		RPG0.store();
		RPG0.internalRead();
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		RPG0.internalStore();
		RPG0.read();
		PC.store();//now PC points to the parameter address
		IR.read();
		RPG0.store();//Back original value for RPG0
		PC.read();//Return pc value for extbus1

	}

	public void addRegMem(){
		//PC++
		RPG0.read();
		IR.store();//Save original value RPG0
		PC.read();
		RPG0.store();
		RPG0.internalRead();
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		RPG0.internalStore();
		RPG0.read();
		PC.store();//now PC points to the parameter address
		IR.read();
		RPG0.store();//Back original value for RPG0
		PC.read();//Return pc value for extbus1

		//Get Reg
		memory.read();
		demux.put(extbus1.get());
		registersInternalRead();
		ula.internalStore(0);

		//PC++
		RPG0.read();
		IR.store();//Save original value RPG0
		PC.read();
		RPG0.store();
		RPG0.internalRead();
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		RPG0.internalStore();
		RPG0.read();
		PC.store();//now PC points to the parameter address
		IR.read();
		RPG0.store();//Back original value for RPG0
		PC.read();//Return pc value for extbus1

		//Get Mem
		RPG0.read();
		IR.store();
		PC.read();
		memory.read();
		memory.read();
		RPG0.store();
		RPG0.internalRead();
		ula.internalStore(1);

		//Add
		ula.add();
		ula.internalRead(1);
		setStatusFlags(intbus1.get());
	
		//Back value for memory
		RPG0.internalStore();
		PC.read();
		memory.read();
		memory.store();
		RPG0.read();
		memory.store();
		IR.read();
		RPG0.store();

		//PC++
		RPG0.read();
		IR.store();//Save original value RPG0
		PC.read();
		RPG0.store();
		RPG0.internalRead();
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		RPG0.internalStore();
		RPG0.read();
		PC.store();//now PC points to the parameter address
		IR.read();
		RPG0.store();//Back original value for RPG0
		PC.read();//Return pc value for extbus1
	}
	

	/**
	 * This method implements the microprogram for
	 * 					SUB address
	 * In the machine language this command number is 1, and the address is in the position next to him
	 *    
	 * where address is a valid position in this memory architecture
	 * The method reads the value from memory (position address) and 
	 * performs an SUB with this value and that one stored in the rpg (the first register in the register list).
	 * The final result must be in RPG (the first register in the register list).
	 * @param address
	 */
	public void subRegReg() {
		//PC++
		RPG0.read();
		IR.store();//Save original value RPG0
		PC.read();
		RPG0.store();
		RPG0.internalRead();
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		RPG0.internalStore();
		RPG0.read();
		PC.store();//now PC points to the parameter address
		IR.read();
		RPG0.store();//Back original value for RPG0
		PC.read();//Return pc value for extbus1
		
		//Get <Reg1>
		memory.read();
		demux.put(extbus1.get());
		registersInternalRead();
		ula.internalStore(0);

		//PC++
		RPG0.read();
		IR.store();//Save original value RPG0
		PC.read();
		RPG0.store();
		RPG0.internalRead();
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		RPG0.internalStore();
		RPG0.read();
		PC.store();//now PC points to the parameter address
		IR.read();
		RPG0.store();//Back original value for RPG0
		PC.read();//Return pc value for extbus1

		//Get <Reg2>
		memory.read();
		demux.put(extbus1.get());
		registersInternalRead();
		ula.internalStore(1);

		//Sub
		ula.sub();
		ula.internalRead(1);
		setStatusFlags(intbus1.get());
		registersInternalStore();
		
		//PC++
		RPG0.read();
		IR.store();//Save original value RPG0
		PC.read();
		RPG0.store();
		RPG0.internalRead();
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		RPG0.internalStore();
		RPG0.read();
		PC.store();//now PC points to the parameter address
		IR.read();
		RPG0.store();//Back original value for RPG0
		PC.read();//Return pc value for extbus1
	}
	
	public void subMemReg(){
		//PC++
		RPG0.read();
		IR.store();//Save original value RPG0
		PC.read();
		RPG0.store();
		RPG0.internalRead();
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		RPG0.internalStore();
		RPG0.read();
		PC.store();//now PC points to the parameter address
		IR.read();
		RPG0.store();//Back original value for RPG0
		PC.read();//Return pc value for extbus1

		//Get Mem
		RPG0.read();
		IR.store();
		PC.read();
		memory.read();
		memory.read();
		RPG0.store();
		RPG0.internalRead();
		ula.internalStore(0);
		IR.read();
		RPG0.store();

		//PC++
		RPG0.read();
		IR.store();//Save original value RPG0
		PC.read();
		RPG0.store();
		RPG0.internalRead();
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		RPG0.internalStore();
		RPG0.read();
		PC.store();//now PC points to the parameter address
		IR.read();
		RPG0.store();//Back original value for RPG0
		PC.read();//Return pc value for extbus1

		//Get Reg
		memory.read();
		demux.put(extbus1.get());
		registersInternalRead();
		ula.internalStore(1);

		//Sub
		ula.sub();
		ula.internalRead(1);
		setStatusFlags(intbus1.get());
		registersInternalStore();

		//PC++
		RPG0.read();
		IR.store();//Save original value RPG0
		PC.read();
		RPG0.store();
		RPG0.internalRead();
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		RPG0.internalStore();
		RPG0.read();
		PC.store();//now PC points to the parameter address
		IR.read();
		RPG0.store();//Back original value for RPG0
		PC.read();//Return pc value for extbus1
	}

	public void subRegMem(){
		//PC++
		RPG0.read();
		IR.store();//Save original value RPG0
		PC.read();
		RPG0.store();
		RPG0.internalRead();
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		RPG0.internalStore();
		RPG0.read();
		PC.store();//now PC points to the parameter address
		IR.read();
		RPG0.store();//Back original value for RPG0
		PC.read();//Return pc value for extbus1

		//Get Reg
		memory.read();
		demux.put(extbus1.get());
		registersInternalRead();
		ula.internalStore(0);

		//PC++
		RPG0.read();
		IR.store();//Save original value RPG0
		PC.read();
		RPG0.store();
		RPG0.internalRead();
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		RPG0.internalStore();
		RPG0.read();
		PC.store();//now PC points to the parameter address
		IR.read();
		RPG0.store();//Back original value for RPG0
		PC.read();//Return pc value for extbus1

		//Get Mem
		RPG0.read();
		IR.store();
		PC.read();
		memory.read();
		memory.read();
		RPG0.store();
		RPG0.internalRead();
		ula.internalStore(1);
		
		//Sub
		ula.sub();
		ula.internalRead(1);
		setStatusFlags(intbus1.get());
	
		//Back value for memory
		RPG0.internalStore();
		PC.read();
		memory.read();
		memory.store();
		RPG0.read();
		memory.store();
		IR.read();
		RPG0.store();

		//PC++
		RPG0.read();
		IR.store();//Save original value RPG0
		PC.read();
		RPG0.store();
		RPG0.internalRead();
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		RPG0.internalStore();
		RPG0.read();
		PC.store();//now PC points to the parameter address
		IR.read();
		RPG0.store();//Back original value for RPG0
		PC.read();//Return pc value for extbus1
	}

	public void imulMemReg() {
	}

	public void imulRegMem() {
	}

	public void imulRegReg() {
	}

	public void moveMemReg() {
		//PC++
		RPG0.read();
		IR.store();//Save original value RPG0
		PC.read();
		RPG0.store();
		RPG0.internalRead();
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		RPG0.internalStore();
		RPG0.read();
		PC.store();//now PC points to the parameter address
		IR.read();
		RPG0.store();//Back original value for RPG0
		PC.read();//Return pc value for extbus1

		// tive que usar a ula, pq toda vez que fazia pc++ destruia o dado que eu peguei da mem
		RPG0.read();
		IR.store();
		PC.read();
		memory.read();
		memory.read();
		RPG0.store();
		RPG0.internalRead();
		ula.internalStore(0);
		IR.read();
		RPG0.store();

		//PC++
		RPG0.read();
		IR.store();//Save original value RPG0
		PC.read();
		RPG0.store();
		RPG0.internalRead();
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		RPG0.internalStore();
		RPG0.read();
		PC.store();//now PC points to the parameter address
		IR.read();
		RPG0.store();//Back original value for RPG0
		PC.read();//Return pc value for extbus1

		//Get Reg
		ula.internalRead(0);
		memory.read();
		demux.put(extbus1.get());
		registersInternalStore();

		//PC++
		RPG0.read();
		IR.store();//Save original value RPG0
		PC.read();
		RPG0.store();
		RPG0.internalRead();
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		RPG0.internalStore();
		RPG0.read();
		PC.store();//now PC points to the parameter address
		IR.read();
		RPG0.store();//Back original value for RPG0
		PC.read();//Return pc value for extbus1
	}

	public void moveRegMem() {
		//PC++
		RPG0.read();
		IR.store();//Save original value RPG0
		PC.read();
		RPG0.store();
		RPG0.internalRead();
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		RPG0.internalStore();
		RPG0.read();
		PC.store();//now PC points to the parameter address
		IR.read();
		RPG0.store();//Back original value for RPG0
		PC.read();//Return pc value for extbus1

		//Get Reg
		memory.read();
		demux.put(extbus1.get());

		//PC++
		RPG0.read();
		IR.store();//Save original value RPG0
		PC.read();
		RPG0.store();
		RPG0.internalRead();
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		RPG0.internalStore();
		RPG0.read();
		PC.store();//now PC points to the parameter address
		IR.read();
		RPG0.store();//Back original value for RPG0
		PC.read();//Return pc value for extbus1

		//Get Mem
		memory.read();
		memory.store();
		registersRead();
		memory.store();

		//PC++
		RPG0.read();
		IR.store();//Save original value RPG0
		PC.read();
		RPG0.store();
		RPG0.internalRead();
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		RPG0.internalStore();
		RPG0.read();
		PC.store();//now PC points to the parameter address
		IR.read();
		RPG0.store();//Back original value for RPG0
		PC.read();//Return pc value for extbus1
	}

	public void moveRegReg() {
		//PC++
		RPG0.read();
		IR.store();//Save original value RPG0
		PC.read();
		RPG0.store();
		RPG0.internalRead();
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		RPG0.internalStore();
		RPG0.read();
		PC.store();//now PC points to the parameter address
		IR.read();
		RPG0.store();//Back original value for RPG0
		PC.read();//Return pc value for extbus1

		//Get Reg1
		memory.read();
		demux.put(extbus1.get());
		registersInternalRead();
		ula.internalStore(0);

		//PC++
		RPG0.read();
		IR.store();//Save original value RPG0
		PC.read();
		RPG0.store();
		RPG0.internalRead();
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		RPG0.internalStore();
		RPG0.read();
		PC.store();//now PC points to the parameter address
		IR.read();
		RPG0.store();//Back original value for RPG0
		PC.read();//Return pc value for extbus1

		//Get Reg2
		memory.read();
		ula.internalRead(0);
		demux.put(extbus1.get());
		registersInternalStore();

		//PC++
		RPG0.read();
		IR.store();//Save original value RPG0
		PC.read();
		RPG0.store();
		RPG0.internalRead();
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		RPG0.internalStore();
		RPG0.read();
		PC.store();//now PC points to the parameter address
		IR.read();
		RPG0.store();//Back original value for RPG0
		PC.read();//Return pc value for extbus1
	}

	public void moveImmReg() {
		//PC++
		RPG0.read();
		IR.store();//Save original value RPG0
		PC.read();
		RPG0.store();
		RPG0.internalRead();
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		RPG0.internalStore();
		RPG0.read();
		PC.store();//now PC points to the parameter address
		IR.read();
		RPG0.store();//Back original value for RPG0
		PC.read();//Return pc value for extbus1

		//Get Imm
		RPG0.read();
		IR.store();
		PC.read();
		memory.read();
		RPG0.store();
		RPG0.internalRead();
		ula.internalStore(0);
		IR.read();
		RPG0.store();

		//PC++
		RPG0.read();
		IR.store();//Save original value RPG0
		PC.read();
		RPG0.store();
		RPG0.internalRead();
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		RPG0.internalStore();
		RPG0.read();
		PC.store();//now PC points to the parameter address
		IR.read();
		RPG0.store();//Back original value for RPG0
		PC.read();//Return pc value for extbus1

		//Get Reg
		memory.read();
		ula.internalRead(0);
		demux.put(extbus1.get());
		registersInternalStore();

		//PC++
		RPG0.read();
		IR.store();//Save original value RPG0
		PC.read();
		RPG0.store();
		RPG0.internalRead();
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		RPG0.internalStore();
		RPG0.read();
		PC.store();//now PC points to the parameter address
		IR.read();
		RPG0.store();//Back original value for RPG0
		PC.read();//Return pc value for extbus1
	}

	public void incReg() {
		// PC++
		RPG0.read();
		IR.store();//Saves original value RPG0 in IR
		PC.read();
		RPG0.store();
		RPG0.internalRead();
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		RPG0.internalStore();
		RPG0.read();
		PC.store(); //now PC points to the parameter address
		IR.read();
		RPG0.store();//Back original value for RPG0
		PC.read();//Return pc value for extbus1
		
		//Get <Reg1>
		memory.read();
		demux.put(extbus1.get());
		registersInternalRead();
		ula.internalStore(1);

		//INC
		ula.inc();
		ula.internalRead(1);
		setStatusFlags(intbus1.get());
		registersInternalStore();
		
		//PC++
		RPG0.read();
		IR.store();//Save original value RPG0
		PC.read();
		RPG0.store();
		RPG0.internalRead();
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		RPG0.internalStore();
		RPG0.read();
		PC.store();//now PC points to the parameter address
		IR.read();
		RPG0.store();//Back original value for RPG0
	}

	public void incMem() {
		// PC++
		RPG0.read();
		IR.store();//Saves original value RPG0 in IR
		PC.read();
		RPG0.store();
		RPG0.internalRead();
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		RPG0.internalStore();
		RPG0.read();
		PC.store(); //now PC points to the parameter address
		IR.read();
		RPG0.store();//Back original value for RPG0
		PC.read();//Return pc value for extbus1
		
		//Get Mem
		RPG0.read();
		IR.store();
		PC.read();
		memory.read();
		memory.read();
		RPG0.store();
		RPG0.internalRead();
		ula.internalStore(1);

		// INC
		ula.inc();
		ula.internalRead(1);
		RPG0.internalStore();

		//Back value to memory
		PC.read();
		memory.read();
		memory.store();
		RPG0.read();
		memory.store();
		IR.read();
		RPG0.store();

		// PC++
		RPG0.read();
		IR.store();//Saves original value RPG0 in IR
		PC.read();
		RPG0.store();
		RPG0.internalRead();
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		RPG0.internalStore();
		RPG0.read();
		PC.store(); //now PC points to the parameter address
		IR.read();
		RPG0.store();//Back original value for RPG0
		PC.read();//Return pc value for extbus1
	}

	/**
	 * This method implements the microprogram for
	 * 					JMP address
	 * In the machine language this command number is 2, and the address is in the position next to him
	 *    
	 * where address is a valid position in this memory architecture (where the PC is redirecto to)
	 * The method reads the value from memory (position address) and 
	 * inserts it into the PC register.
	 * So, the program is deviated
	 * @param address
	 */
	public void jmp() {
		//PC++
		RPG0.read();
		IR.store();
		PC.read();
		RPG0.store();
		RPG0.internalRead();
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		RPG0.internalStore();
		RPG0.read();
		PC.store();

		// JMP
		PC.read();
		memory.read();
		PC.store();

		// return RPG0 value
		IR.read();
		RPG0.store();
	}
	
	/**
	 * This method implements the microprogram for
	 * 					jn address
	 * In the machine language this command number is 4, and the address is in the position next to him
	 *    
	 * where address is a valid position in this memory architecture (where 
	 * the PC is redirected to, but only in the case the NEGATIVE bit in Flags is 1)
	 * The method reads the value from memory (position address) and 
	 * inserts it into the PC register if the NEG bit in Flags register is setted.
	 * So, the program is deviated conditionally
	 */
	public void jn() {
		//PC++
		RPG0.read();
		IR.store();
		PC.read();
		RPG0.store();
		RPG0.internalRead();
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		RPG0.internalStore();
		RPG0.read();
		PC.store();

		// JN
		if (Flags.getBit(1) == 1) {
			PC.read();
			memory.read();
			PC.store();
		}
		// PC++
		else {
			ula.inc();
			ula.internalRead(1);
			RPG0.internalStore();
			RPG0.read();
			PC.store();
		}
		// return RPG0 value
		IR.read();
		RPG0.store();
	}
	
	/**
	 * This method implements the microprogram for
	 * 					JZ address
	 * In the machine language this command number is 3, and the address is in the position next to him
	 *    
	 * where address is a valid position in this memory architecture (where 
	 * the PC is redirected to, but only in the case the ZERO bit in Flags is 1)
	 * The method reads the value from memory (position address) and 
	 * inserts it into the PC register if the ZERO bit in Flags register is setted.
	 * So, the program is deviated conditionally
	 * @param address
	 */
	public void jz() {
		// PC++
		RPG0.read();
		IR.store();
		PC.read();
		RPG0.store();
		RPG0.internalRead();
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		RPG0.internalStore();
		RPG0.read();
		PC.store();

		// JZ
		if (Flags.getBit(0) == 1) {
			PC.read();
			memory.read();
			PC.store();
		}
		// PC++
		else {
			ula.inc();
			ula.internalRead(1);
			RPG0.internalStore();
			RPG0.read();
			PC.store();
		}
		// return RPG0 value
		IR.read();
		RPG0.store();
	}

	public void jnz() {
		// PC++
		RPG0.read();
		IR.store();
		PC.read();
		RPG0.store();
		RPG0.internalRead();
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		RPG0.internalStore();
		RPG0.read();
		PC.store();

		// JNZ
		if (Flags.getBit(0) == 0) {
			PC.read();
			memory.read();
			PC.store();
		}
		// PC++
		else {
			ula.inc();
			ula.internalRead(1);
			RPG0.internalStore();
			RPG0.read();
			PC.store();
		}
		// return RPG0 value
		IR.read();
		RPG0.store();
	}

	public void jeq() {
		// PC++
		RPG0.read();
		IR.store();
		PC.read();
		RPG0.store();
		RPG0.internalRead();
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		RPG0.internalStore();
		RPG0.read();
		PC.store();
		IR.read();
		RPG0.store();
		PC.read();

		//Get <Reg1>
		memory.read();
		demux.put(extbus1.get());
		registersInternalRead();
		ula.internalStore(0);

		// PC++
		RPG0.read();
		IR.store();
		PC.read();
		RPG0.store();
		RPG0.internalRead();
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		RPG0.internalStore();
		RPG0.read();
		PC.store();
		IR.read();
		RPG0.store();
		PC.read();

		//Get <Reg2>
		memory.read();
		demux.put(extbus1.get());
		registersInternalRead();
		ula.internalStore(1);

		//Sub <Reg1> - <Reg2>
		ula.sub();
		ula.internalRead(1);
		setStatusFlags(intbus1.get());

		// PC++
		RPG0.read();
		IR.store();
		PC.read();
		RPG0.store();
		RPG0.internalRead();
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		RPG0.internalStore();
		RPG0.read();
		PC.store();

		// JZ
		if (Flags.getBit(0) == 1) {
			PC.read();
			memory.read();
			PC.store();
		}
		// PC++
		else {
			ula.inc();
			ula.internalRead(1);
			RPG0.internalStore();
			RPG0.read();
			PC.store();
		}
		// return RPG0 value
		IR.read();
		RPG0.store();
	}

	public void jgt() {
		// PC++
		RPG0.read();
		IR.store();
		PC.read();
		RPG0.store();
		RPG0.internalRead();
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		RPG0.internalStore();
		RPG0.read();
		PC.store();
		IR.read();
		RPG0.store();
		PC.read();

		//Get <Reg1>
		memory.read();
		demux.put(extbus1.get());
		registersInternalRead();
		ula.internalStore(0);

		// PC++
		RPG0.read();
		IR.store();
		PC.read();
		RPG0.store();
		RPG0.internalRead();
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		RPG0.internalStore();
		RPG0.read();
		PC.store();
		IR.read();
		RPG0.store();
		PC.read();

		//Get <Reg2>
		memory.read();
		demux.put(extbus1.get());
		registersInternalRead();
		ula.internalStore(1);

		//Sub <Reg1> - <Reg2> --> <Reg2>
		ula.sub();
		ula.internalRead(1);
		setStatusFlags(intbus1.get());

		// PC++
		RPG0.read();
		IR.store();
		PC.read();
		RPG0.store();
		RPG0.internalRead();
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		RPG0.internalStore();
		RPG0.read();
		PC.store();

		// J(!N && !Z)
		if (Flags.getBit(1) == 0 && Flags.getBit(0) == 0) {
			PC.read();
			memory.read();
			PC.store();
		}
		// PC++
		else {
			ula.inc();
			ula.internalRead(1);
			RPG0.internalStore();
			RPG0.read();
			PC.store();
		}
		// return RPG0 value
		IR.read();
		RPG0.store();
	}

	public void jlw() {
		// PC++
		RPG0.read();
		IR.store();
		PC.read();
		RPG0.store();
		RPG0.internalRead();
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		RPG0.internalStore();
		RPG0.read();
		PC.store();
		IR.read();
		RPG0.store();
		PC.read();

		//Get <Reg1>
		memory.read();
		demux.put(extbus1.get());
		registersInternalRead();
		ula.internalStore(0);

		// PC++
		RPG0.read();
		IR.store();
		PC.read();
		RPG0.store();
		RPG0.internalRead();
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		RPG0.internalStore();
		RPG0.read();
		PC.store();
		IR.read();
		RPG0.store();
		PC.read();

		//Get <Reg2>
		memory.read();
		demux.put(extbus1.get());
		registersInternalRead();
		ula.internalStore(1);

		//Sub <Reg1> - <Reg2> 
		ula.sub();
		ula.internalRead(1);
		setStatusFlags(intbus1.get());

		// PC++
		RPG0.read();
		IR.store();
		PC.read();
		RPG0.store();
		RPG0.internalRead();
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		RPG0.internalStore();
		RPG0.read();
		PC.store();

		// JN
		if (Flags.getBit(1) == 1) {
			PC.read();
			memory.read();
			PC.store();
		}
		// PC++
		else {
			ula.inc();
			ula.internalRead(1);
			RPG0.internalStore();
			RPG0.read();
			PC.store();
		}
		// return RPG0 value
		IR.read();
		RPG0.store();
	}
		
	public ArrayList<Register> getRegistersList() {
		return registersList;
	}

	/**
	 * This method performs an (external) read from a register into the register list.
	 * The register id must be in the demux bus
	 */
	private void registersRead() {
		registersList.get(demux.get()).read();
	}
	
	/**
	 * This method performs an (internal) read from a register into the register list.
	 * The register id must be in the demux bus
	 */
	private void registersInternalRead() {
		registersList.get(demux.get()).internalRead();;
	}
	
	/**
	 * This method performs an (external) store toa register into the register list.
	 * The register id must be in the demux bus
	 */
	private void registersStore() {
		registersList.get(demux.get()).store();
	}
	
	/**
	 * This method performs an (internal) store toa register into the register list.
	 * The register id must be in the demux bus
	 */
	private void registersInternalStore() {
		registersList.get(demux.get()).internalStore();;
	}



	/**
	 * This method reads an entire file in machine code and
	 * stores it into the memory
	 * NOT TESTED
	 * @param filename
	 * @throws IOException 
	 */
	public void readExec(String filename) throws IOException {
		   BufferedReader br = new BufferedReader(new		 
		   FileReader(filename+".dxf"));
		   String linha;
		   int i=0;
		   while ((linha = br.readLine()) != null) {
			     extbus1.put(i);
			     memory.store();
			   	 extbus1.put(Integer.parseInt(linha));
			     memory.store();
			     i++;
			}
			br.close();
	}
	
	/**
	 * This method executes a program that is stored in the memory
	 */
	public void controlUnitEexec() {
		halt = false;
		while (!halt) {
			fetch();
			decodeExecute();
		}

	}
	

	/**
	 * This method implements The decode proccess,
	 * that is to find the correct operation do be executed
	 * according the command.
	 * And the execute proccess, that is the execution itself of the command
	 */
	private void decodeExecute() {
		IR.internalRead(); //the instruction is in the internalbus2
		int command = intbus1.get();
		simulationDecodeExecuteBefore(command);
		switch (command) {
		case 0:
			addRegReg();
			break;
		case 1:
			addMemReg();
			break;
		case 2:
			addRegMem();
			break;
		case 3:
			subRegReg();
			break;
		case 4:
			subMemReg();
			break;
		case 5:
			subRegMem();
			break;
		case 6:
			imulMemReg();
			break;
		case 7:
			imulRegMem();
			break;
		case 8:
			imulRegReg();
			break;
		case 9:
			moveMemReg();
			break;
		case 10:
			moveRegMem();
			break;
		case 11:
			moveRegReg();
			break;
		case 12:
			moveImmReg();
			break;
		case 13:
			incReg();
			break;
		case 14:
			incMem();
			break;
		case 15:
			jmp();
			break;
		case 16:
			jn();
			break;
		case 17:
			jz();
			break;
		case 18:
			jnz();
			break;
		case 19:
			jeq();
			break;
		case 20:
			jgt();
			break;
		case 21:
			jlw();
			break;
			
		default:
			halt = true;
			break;
		}
		if (simulation)
			simulationDecodeExecuteAfter();
	}

	/**
	 * This method is used to show the components status in simulation conditions
	 * NOT TESTED
	 * @param command 
	 */
	private void simulationDecodeExecuteBefore(int command) {
		System.out.println("----------BEFORE Decode and Execute phases--------------");
		String instruction;
		int parameter = 0;
		for (Register r:registersList) {
			System.out.println(r.getRegisterName()+": "+r.getData());
		}
		if (command !=-1)
			instruction = commandsList.get(command);
		else
			instruction = "END";
		if (hasOperands(instruction)) {
			parameter = memory.getDataList()[PC.getData()+1];
			System.out.println("Instruction: "+instruction+" "+parameter);
		}
		else
			System.out.println("Instruction: "+instruction);
		if ("read".equals(instruction))
			System.out.println("memory["+parameter+"]="+memory.getDataList()[parameter]);
		
	}

	/**
	 * This method is used to show the components status in simulation conditions
	 * NOT TESTED 
	 */
	private void simulationDecodeExecuteAfter() {
		String instruction;
		System.out.println("-----------AFTER Decode and Execute phases--------------");
		System.out.println("Internal Bus 1: "+intbus1.get());
		System.out.println("Internal Bus 2: "+intbus1.get());
		System.out.println("External Bus 1: "+extbus1.get());
		for (Register r:registersList) {
			System.out.println(r.getRegisterName()+": "+r.getData());
		}
		Scanner entrada = new Scanner(System.in);
		System.out.println("Press <Enter>");
		String mensagem = entrada.nextLine();
	}

	/**
	 * This method uses PC to find, in the memory,
	 * the command code that must be executed.
	 * This command must be stored in IR
	 * NOT TESTED!
	 */
	private void fetch() {
		PC.read();
		memory.read();
		IR.store();
		simulationFetch();
	}

	/**
	 * This method is used to show the components status in simulation conditions
	 * NOT TESTED!!!!!!!!!
	 */
	private void simulationFetch() {
		if (simulation) {
			System.out.println("-------Fetch Phase------");
			System.out.println("PC: "+PC.getData());
			System.out.println("IR: "+IR.getData());
		}
	}

	/**
	 * This method is used to show in a correct way the operands (if there is any) of instruction,
	 * when in simulation mode
	 * NOT TESTED!!!!!
	 * @param instruction 
	 * @return
	 */
	private boolean hasOperands(String instruction) {
		if ("inc".equals(instruction)) //inc is the only one instruction having no operands
			return false;
		else
			return true;
	}

	/**
	 * This method returns the amount of positions allowed in the memory
	 * of this architecture
	 * NOT TESTED!!!!!!!
	 * @return
	 */
	public int getMemorySize() {
		return memorySize;
	}
	
	public static void main(String[] args) throws IOException {
		Architecture1 arch = new Architecture1(true);
		arch.readExec("program");
		arch.controlUnitEexec();
	}
	

}
