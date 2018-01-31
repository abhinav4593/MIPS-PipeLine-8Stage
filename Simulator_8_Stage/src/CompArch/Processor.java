package CompArch;

import java.util.ArrayList;

import Managers.IOManager;
import Managers.RegisterManager;
/**
 * @author Abhinav Gundlapalli
 * @id 1454321
 * @email agundlapalli@uh.edu
 */
public class Processor {
	private String initialData;
	public enum Instruction_Name {
		LD, SD, DADD, SUB, BNEZ
	}

	public enum Instruction_Type {
		MEMORY, ALU_INST, BRANCH
	}

	public enum Instruction_Status {
		INQUE, BUSY, RDY, STALLED, DELETED, COMPLETE
	}

	public enum Instruction_Add_Type {
		REG_REGISTER, REG_IMMEDIATE
	};

	public enum Cycles {
		IF1, IF2, ID, EX, MEM1, MEM2, MEM3, WB
	};

	private int instNum;
	private Instruction_Name name;
	private Instruction_Type type;
	private Instruction_Add_Type mode;
	private Instruction_Status InsStatus;
	private Cycles Cycle;
	private ControlFlags ctrl_Var = new ControlFlags();
	private static ArrayList<String> insQList = new ArrayList<String>();
	private RegisterManager Source_Reg1,Source_Reg2,Destination_Reg; 
	private String bzne_Target;

	public Processor(String rt) {
		this.initialData = rt;
		this.instNum = Simulator.getInstnum();
		Simulator.setInstnum(Simulator.getInstnum() + 1);
	}

	public Instruction_Name Instruction_Check(String s) {
		for (Instruction_Name i : Instruction_Name.values()) {
			if (i.name().equals(s)) {
				return i;
			}
		}
		return null;
	}

	public void Current_Stage() {
		if (this.InsStatus == Instruction_Status.INQUE || this.Cycle == null)
			return;
		boolean var = true;
		String strInq = "";
		String cycleStr, str1 = "**";
		cycleStr = "I" + instNum + "-" + Cycle.toString();
		cycleStr.trim();
		if (var)
			strInq = strInq.concat(Instruction_Status.INQUE.toString());
		else
			var = false;

		for (int i = 0; i < 8 - cycleStr.length(); i++) {
			str1 = str1.concat("*");
		}
		cycleStr += str1;
		cycleStr = cycleStr.replace('*', ' ');
		IOManager.fileOut(cycleStr, false);
	}
	
	private void Instruction_Parser(String inst) {
		inst = inst.trim();
		String delims = "[ ,]+";
		String[] tokens = inst.split(delims);
		int len = tokens.length;
		name = Instruction_Check(tokens[0]);
		if (name == null) {
			System.err.println("Instruction" + inst + "Cant be parserd");
		}
		if ((name == Instruction_Name.LD || name == Instruction_Name.SD || name == Instruction_Name.BNEZ) && len != 3)																												// Op2)
			System.err.println("Insufficient arguments for instruction " + name.toString());
		else if ((name == Instruction_Name.DADD || name == Instruction_Name.SUB) && len != 4)
			System.err.println("Insufficient arguments to the instruction " + name.toString());
		switch (name) {
		case LD:
			Source_Reg1 = new RegisterManager(tokens[2]);
			Source_Reg2 = null;
			Destination_Reg = new RegisterManager(tokens[1]);
			break;
		case SD:
			Source_Reg1 = new RegisterManager(tokens[1]);
			Source_Reg2 = null;
			Destination_Reg = new RegisterManager(tokens[2]);
			break;
		case DADD:
			Source_Reg1 = new RegisterManager(tokens[2]);
			Source_Reg2 = new RegisterManager(tokens[3]);
			Destination_Reg = new RegisterManager(tokens[1]);
			break;
		case SUB:
			Source_Reg1 = new RegisterManager(tokens[2]);
			Source_Reg2 = new RegisterManager(tokens[3]);
			Destination_Reg = new RegisterManager(tokens[1]);
			break;
		case BNEZ:
			Source_Reg1 = new RegisterManager(tokens[1]);
			bzne_Target = tokens[2].trim();
			break;
		default:
			break;
		}
		ctrl_Var = ControlFlags.setFlags(ctrl_Var, name);
	}

	public boolean Register_Write(int val) {
		if (ctrl_Var.isWritetoaReg()) {
			return IOManager.Reg_Val_Put(Destination_Reg.getReferenceReg(), val);
		} else {
			return true;
		}
	}

	public void decode() {
		if (this.initialData != null)
			Instruction_Parser(initialData);
		else
			System.err.println("Empty Instruction can't be decoded");
	}

	public void nxtCycleProcessor() {
		if (this.InsStatus != Instruction_Status.COMPLETE) {
			if (this.Cycle != null) {
				this.Cycle = this.Cycle.ordinal() < Cycles.values().length - 1
						? Cycles.values()[this.Cycle.ordinal() + 1] : null;
			} else {
				System.err.println("Error in getting next stage of instruction " + initialData);
			}
		}
	}
	
	public final String getInitData() {
		return initialData;
	}

	public final void setInitData(String initData) {
		this.initialData = initData;
	}

	public final int getInstNum() {
		return instNum;
	}

	public final void setInstNum(int instNum) {
		this.instNum = instNum;
	}

	public final Instruction_Name getName() {
		return name;
	}

	public final void setName(Instruction_Name name) {
		this.name = name;
	}

	public final Instruction_Type getType() {
		return type;
	}

	public final void setType(Instruction_Type type) {
		this.type = type;
	}

	public final Instruction_Add_Type getMode() {
		return mode;
	}

	public final void setMode(Instruction_Add_Type mode) {
		this.mode = mode;
	}

	public final Instruction_Status getStatus() {
		return InsStatus;
	}

	public final void setStatus(Instruction_Status status) {
		this.InsStatus = status;
	}

	public final Cycles getCurCycle() {
		return Cycle;
	}

	public final void setCurCycle(Cycles curCycle) {
		this.Cycle = curCycle;
	}

	public final ControlFlags getCntrl() {
		return ctrl_Var;
	}

	public final void setCntrl(ControlFlags cntrl) {
		this.ctrl_Var = cntrl;
	}

	public static final ArrayList<String> getInsQ() {
		return insQList;
	}

	public static final void setInsQu(ArrayList<String> insQu) {
		Processor.insQList = insQu;
	}

	public final RegisterManager getSrc1() {
		return Source_Reg1;
	}

	public final void setSrc1(RegisterManager src1) {
		this.Source_Reg1 = src1;
	}

	public final RegisterManager getSrc2() {
		return Source_Reg2;
	}

	public final void setSrc2(RegisterManager src2) {
		this.Source_Reg2 = src2;
	}

	public final RegisterManager getDest() {
		return Destination_Reg;
	}

	public final void setDest(RegisterManager dest) {
		this.Destination_Reg = dest;
	}

	public final String getBranchTarget() {
		return bzne_Target;
	}

	public final void setBranchTarget(String branchTarget) {
		this.bzne_Target = branchTarget;
	}
}