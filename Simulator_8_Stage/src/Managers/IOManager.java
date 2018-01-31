package Managers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import CompArch.Pipeline;
import CompArch.Processor;
import CompArch.Simulator;

/**
 * @author Abhinav Gundlapalli
 * @id 1454321
 * @email agundlapalli@uh.edu
 */
public class IOManager {

	public static StringBuffer outPutContent = new StringBuffer();

	public enum IPSplWords {
		REGISTERS, MEMORY, CODE
	};

	private int nextInst = 0;
	public IPSplWords IPType;

	public static void writeOutFile() {
		try {
			File file = new File(Simulator.getOutFile());
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fileWri = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bufWri = new BufferedWriter(fileWri);
			bufWri.write(outPutContent.toString());
			bufWri.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void toOutfile(String s) {
		fileOut(s, true);
	}

	public static boolean Reg_Val_Set(String reg, int newVal) {
		int regNum = Integer.parseInt(reg.substring(1, reg.length() - 1));
		return Reg_Val_Put(regNum, newVal);
	}

	public void ReadInput(String fileName) {
		BufferedReader br = null;
		try {
			String Line;
			br = new BufferedReader(new FileReader(fileName));
			while ((Line = br.readLine()) != null) {
				Input_Parse(Line);
			}
		} catch (IOException e) {
			System.err.println("There was an error reading from the specified input file");
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public static Integer getRegValue(String reg) {
		int regNum = Integer.parseInt(reg.substring(1, reg.length() - 1));
		return getRegValue(regNum);
	}

	public IPSplWords keyword_Checker(String s) {
		for (IPSplWords k : IPSplWords.values()) {
			if (k.name().equals(s)) {
				return k;
			}
		}
		return null;
	}

	public static void fileOut(String s, boolean y) {
		try {
			if (y)
				outPutContent.append(s + "\n");
			else
				outPutContent.append(s);
		} catch (Exception e) {
			System.err.println(
					"JVM out of memory! Trying to append to the output.\n    Make sure there's not an infinite loop with branches!");
			System.exit(1);
		}
	}

	public static boolean Reg_Val_Put(int regNum, int newVal) {
		if (regNum < 0 || regNum > 31) {
			System.err.println("Register that falls out of bounds: " + regNum);
			return false;
		}
		if (RegisterManager.getRegisters()[regNum] == null) {
			RegisterManager thisReg = new RegisterManager(regNum, newVal);
			RegisterManager.getRegisters()[regNum] = thisReg;
			return true;
		}
		RegisterManager theReg = RegisterManager.getRegisters()[regNum];
		return theReg.assignValue(newVal);
	}

	public static Integer getRegValue(int reg) {
		RegisterManager RegObj;
		if (reg < 0 || reg > 31)
			System.err.println("register vale is out of bounds:" + reg);
		if (RegisterManager.getRegisters()[reg] == null || reg == 0)
			return 0;
		RegObj = RegisterManager.getRegisters()[reg];
		if (!RegObj.isReady())
			return null;
		return RegObj.valueRead();
	}

	public static void Registers_Write() {
		toOutfile("REGISTERS");
		for (RegisterManager r : RegisterManager.getRegisters()) {
			if (r != null && r.getVal() != 0)
				toOutfile("R" + r.getNo() + " " + r.getVal());
		}
		toOutfile("\rMEMORY");
		Map<Integer, Integer> memMap = new TreeMap<Integer, Integer>(MemoryManager.Mem_Map);
		for (Integer loc : memMap.keySet()) {
			toOutfile(loc + " " + memMap.get(loc));
		}
	}

	public static void Stall_appender(int instNum) {
		String str, str1 = "**";
		str = "I" + instNum + "-Stall";
		str.trim();
		for (int i = 0; i < 8 - str.length(); i++)
			str1 += "*";
		str += str1;
		str = str.replace('*', ' ');
		IOManager.fileOut(str, false);
	}

	public static Pipeline getAddress(int i) {
		if (Pipeline.getPipelines().size() >= i - 1)
			return Pipeline.getPipelines().get(i - 2);
		else
			return null;
	}

	public void Code_Prcessor(String in) {
		int colPos = in.indexOf(":");
		if (colPos > -1) {
			Simulator.getBranchLoc().put(in.substring(0, colPos).trim(), nextInst);
			in = in.substring(colPos + 2, in.length());
		}
		nextInst++;
		Processor.getInsQ().add(in.trim());
	}

	public void Input_Parse(String nxtLine) {
		if (!nxtLine.trim().isEmpty()) {
			IPSplWords tempLine = keyword_Checker(nxtLine.trim());
			if (tempLine != null) {
				IPType = tempLine;
			} else if (IPType == null) {
				System.err.println("Cannot find the keywords (REGISTERS/MEMORY/CODE)!");
				System.exit(1);
			} else {
				switch (IPType) {
				case REGISTERS:
					Reg_Processor(nxtLine);
					break;
				case MEMORY:
					Memo_Processor(nxtLine);
					break;
				case CODE:
					Code_Prcessor(nxtLine);
					break;
				default:
					System.err.println("Invalid Input");
				}
			}
		}
	}

	public void Memo_Processor(String input) {
		String[] identity = input.split("[ ]+");
		int position = Integer.parseInt(identity[0]);
		int value = Integer.parseInt(identity[1]);
		if (position % 8 != 0) {
			System.err.println("Mem loc " + position + " is not a multiple of 8");
			return;
		} else if (position < 0 || position > 992) {

			System.err.println("Mem loc " + position + " Out of range");
			return;
		}
		// add to the bank
		try {
			MemoryManager.Mem_Map.put(position, value);
		} catch (Exception e) {
			System.err.println("Could not add the value " + value + " to mem loc " + position);
		}
		return;
	}

	public void Reg_Processor(String input) {
		String[] identity = input.split("[ ]+");
		String reg = identity[0];
		String val = identity[1];
		int regNum;
		// checking for correct format of incoming register
		if (reg.matches("^R\\d+")) {
			regNum = Integer.parseInt(reg.substring(1));
		} else {

			System.err.println("Register " + reg + " not given in Required format");
			return;
		}
		IOManager.Reg_Val_Put(regNum, Integer.parseInt(val));
	}

}