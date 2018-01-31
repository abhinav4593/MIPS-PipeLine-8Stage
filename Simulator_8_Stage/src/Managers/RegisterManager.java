package Managers;

import CompArch.Pipeline;
import CompArch.Processor.Instruction_Name;

/**
 * @author Abhinav Gundlapalli
 * @id 1454321
 * @email agundlapalli@uh.edu
 */
public class RegisterManager {

	public enum AddModes {
		REGISTER, INDIRECT, DISPLACEMENT, IMMEDIATE
	};

	private AddModes Operation_Mode;
	private Integer valueOffset, Ref_Reg, Updated_Value, Number, value;
	private boolean ready;
	private static RegisterManager[] Registers;

	public RegisterManager(String operand) {
		String[] identity;
		operand = operand.trim();
		Operation_Mode = null;
		valueOffset = null;
		Ref_Reg = null;
		Updated_Value = null;
		if (operand.indexOf('#') != -1)
			Operation_Mode = AddModes.IMMEDIATE;
		else if (operand.matches("\\d+\\(R\\d+\\)"))
			Operation_Mode = AddModes.DISPLACEMENT;
		else if (operand.matches("^\\(R\\d+\\)"))
			Operation_Mode = AddModes.INDIRECT;
		else if (operand.matches("^R\\d+"))
			Operation_Mode = AddModes.REGISTER;
		else
			System.err.println("addressing mode of operand " + operand + "Not found");
		switch (Operation_Mode) {
		case IMMEDIATE:
			Updated_Value = Integer.parseInt(operand.substring(1));
			break;
		case DISPLACEMENT:
			identity = operand.split("[()]+");
			Ref_Reg = get_Integer_Value(identity[1]);
			valueOffset = Integer.parseInt(identity[0]);
			break;
		case INDIRECT:
			identity = operand.split("[()]+");
			Ref_Reg = get_Integer_Value(identity[1]);
			valueOffset = 0;
			break;
		case REGISTER:
			Ref_Reg = get_Integer_Value(operand);
			break;
		default:
			System.err.println("Could not parse the operand :" + operand);
			break;
		}
	}

	public Integer rdOpVal() {
		Integer retVal = null;
		switch (Operation_Mode) {
		case IMMEDIATE:
			retVal = Updated_Value;
			break;
		case DISPLACEMENT:
			retVal = IOManager.getRegValue(Ref_Reg) + valueOffset;
			break;
		case INDIRECT:
			retVal = IOManager.getRegValue(Ref_Reg) + 0;
			break;
		case REGISTER:
			retVal = Reg_Val_Get();
			break;
		default:
			System.err.println("Operand value can't be Determined");
			break;
		}
		return retVal;
	}

	private Integer get_Integer_Value(String reg) {
		Integer regNum = Integer.parseInt(reg.substring(1));
		if (regNum < 0 || regNum > 31) {
			System.err.println("Register Value can't be set: " + regNum + " as it is out of bounds");
			return null;
		}
		return regNum;
	}

	private Integer Reg_Val_Get() {
		Integer valReturned = null;
		for (int i = 7; i > 3; i--) {
			Pipeline objPipeline = IOManager.getAddress(i);
			if (objPipeline != null && objPipeline.getInst().getCntrl().isWritetoaReg()
					&& (objPipeline.getInst().getDest().Ref_Reg == this.Ref_Reg)) {
				if (objPipeline.getInst().getCntrl().isALUOp() && (i > 4))
					valReturned = objPipeline.getALUresult().valueRead();
				else if ((objPipeline.getInst().getName() == Instruction_Name.LD) && (i > 5))
					valReturned = objPipeline.getLdmemDataReg().valueRead();
			}
		}
		if (valReturned == null)
			valReturned = IOManager.getRegValue(Ref_Reg);
		return valReturned;
	}

	public RegisterManager(int n, int val) {
		this.Number = n;
		this.value = val;
		this.ready = true;
	}

	public RegisterManager() {
		this.Number = -1;
		this.value = 0;
		this.ready = true;
	}

	public boolean Reg_Check(int newVal) {
		this.ready = true;
		return assignValue(newVal);
	}

	public int valueRead() {
		return this.value;
	}

	public boolean assignValue(int newVal) {
		if (!this.ready) {
			System.err.println("register value can't be set " + this.Number + " before ready.");
			return false;
		}
		this.value = newVal;
		return true;
	}

	public boolean isReady() {
		return this.ready;
	}

	public void setReady(boolean r) {
		this.ready = r;
	}

	public void flipReady() {
		this.ready = !this.ready;
	}

	public final AddModes getOpMode() {
		return Operation_Mode;
	}

	public final void setOpMode(AddModes opMode) {
		Operation_Mode = opMode;
	}

	public final Integer getValueOffset() {
		return valueOffset;
	}

	public final void setValueOffset(Integer valueOffset) {
		this.valueOffset = valueOffset;
	}

	public final Integer getReferenceReg() {
		return Ref_Reg;
	}

	public final void setReferenceReg(Integer referenceReg) {
		Ref_Reg = referenceReg;
	}

	public final Integer getValUpdated() {
		return Updated_Value;
	}

	public final void setValUpdated(Integer valUpdated) {
		this.Updated_Value = valUpdated;
	}

	public final Integer getNo() {
		return Number;
	}

	public final void setNo(Integer no) {
		Number = no;
	}

	public final Integer getVal() {
		return value;
	}

	public final void setVal(Integer val) {
		this.value = val;
	}

	public final boolean isRdy() {
		return ready;
	}

	public final void setRdy(boolean rdy) {
		this.ready = rdy;
	}

	public static final RegisterManager[] getRegisters() {
		return Registers;
	}

	public static final void setRegisters(RegisterManager[] registers) {
		Registers = registers;
	}
}