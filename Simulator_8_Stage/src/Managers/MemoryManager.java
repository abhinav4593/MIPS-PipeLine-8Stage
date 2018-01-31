package Managers;

import java.util.HashMap;

import CompArch.Pipeline;
import CompArch.Simulator;
import CompArch.Processor.Instruction_Name;

/**
 * @author Abhinav Gundlapalli
 * @id 1454321
 * @email agundlapalli@uh.edu
 */
public class MemoryManager {
	public static HashMap<Integer, Integer> Mem_Map = new HashMap<Integer, Integer>();

	public static Integer getMemoryValue(int loc) {
		if (loc % 8 != 0) {
			System.err.println("Memory location " + loc + " is not a multiple of 8");
			return null;
		} else if (loc < 0 || loc > 992) {

			System.err.println("Memory location " + loc + " falls out of acceptable bounds");
			return null;
		}
		Integer memValue = MemoryManager.Mem_Map.get(loc);
		if (memValue == null)
			return 0;
		else
			return memValue;
	}

	// for correspoding reg value
	public static Integer getMemoryValue(String reg, int offset) {
		int memPos = IOManager.getRegValue(reg);
		memPos += offset;
		return getMemoryValue(memPos);
	}

	public static Integer getMemoryValue(int reg, int offset) {
		int memPos = IOManager.getRegValue(reg);
		memPos += offset;
		return getMemoryValue(memPos);
	}

	public static boolean setMemoryValue(int loc, int value) {
		if (loc % 8 != 0) {
			System.err.println("Memory location " + loc + " is not a multiple of 8");
			return false;
		} else if (loc < 0 || loc > 992) {
			System.err.println("Memory location " + loc + " falls out of acceptable bounds");
			return false;
		}
		try {
			MemoryManager.Mem_Map.put(loc, value);
		} catch (Exception e) {
			System.err.println("Could not add the value " + value + " to memory location " + loc);
			return false;
		}
		return true;
	}

	public static void MEM1() {
		Pipeline mem1 = IOManager.getAddress(5);
		if (mem1 != null) {
			// The PC is updated
			if (mem1.getInst().getName() == Instruction_Name.LD) {
				mem1.getLdmemDataReg().assignValue(getMemoryValue(mem1.getALUresult().valueRead()));
			} else if (mem1.getInst().getName() == Instruction_Name.SD) {
				setMemoryValue(mem1.getALUresult().valueRead(), mem1.getInst().getSrc1().rdOpVal());
			} else if (mem1.getInst().getName() == Instruction_Name.BNEZ) {
				//branch not equal
				if (mem1.getResAftrCond() == true) {
					// PipeLine Clean
					//Pipeline.flushPipeLine();
					// update PC
					Simulator.setPC(Simulator.getBranchLoc().get(mem1.getInst().getBranchTarget()));
				}
			}
			mem1.getInst().nxtCycleProcessor();
			mem1.getInst().Current_Stage();
		}
	}

	public static void MEM2() {
		Pipeline mem2 = IOManager.getAddress(6);
		if (mem2 != null) {
			mem2.getInst().nxtCycleProcessor();
			mem2.getInst().Current_Stage();
		}
	}

	public static void MEM3() {
		Pipeline mem3 = IOManager.getAddress(7);
		if (mem3 != null) {
			mem3.getInst().nxtCycleProcessor();
			mem3.getInst().Current_Stage();
		}
	}

}
