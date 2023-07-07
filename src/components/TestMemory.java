package components;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestMemory {

	@Test
	public void testReadStore() {
		Bus bus = new Bus();
		Memory memory = new Memory(16, bus); //creates a 16 positions memory filled with zeros, attached to the bus
		bus.put(-1);
		assertEquals(-1, bus.get());
		for (int i=0;i<16;i++) {
			bus.put(i);
			memory.read();
			assertEquals(0, bus.get()); //checks if all positions were initialized with zeroes
		}
		//now, inserting numbers into the memory
		for (int i=0;i<16;i++) {
			bus.put(i);
			memory.store(); //the position is defined
			memory.store(); //storing in each position a number equals its address
		}
		//testing if the numbers into the memory are the ones we just inserted
		for (int i=0;i<16;i++) {
			bus.put(i);
			memory.read();
			assertEquals(i, bus.get()); //the value is equals to the position
		}
		//all positions being equals to the square of the position
		for (int i=0;i<16;i++) {
			bus.put(i);
			memory.store(); //setting the position
			bus.put(i*i);
			memory.store(); //storing the data
		}
		//testing if the numbers into the memory are the ones we just inserted
		for (int i=0;i<16;i++) {
			bus.put(i);
			memory.read();
			assertEquals(i*i, bus.get()); //the value is equals to the 2nd power of the position
		}
		
		//trying to access addresses out of the memory range makes no effect into the bus
		bus.put(-5);
		memory.read();
		assertEquals(-5, bus.get());
		
	}


}
