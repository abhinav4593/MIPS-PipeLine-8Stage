package CompArch;

/**
 * @author Abhinav Gundlapalli
 * @id 1454321
 * @email agundlapalli@uh.edu
 */
public class ControlFlags {
	private boolean Read,Reg_Write,ALU_Source,Mem_Read,Mem_Write,memRegWr,ALU_Output,Branch;

	public static ControlFlags setFlags(ControlFlags cntrl, Processor.Instruction_Name name) {
		// inilize
		cntrl.Read = false;
		cntrl.memRegWr = false;
		cntrl.Branch = false;
		cntrl.ALU_Output = false;
		cntrl.Reg_Write = false;
		cntrl.ALU_Source = false;
		cntrl.Mem_Write = false;
		cntrl.Mem_Read = false;
		
		switch (name) {
		case DADD:
			cntrl.Reg_Write = true;
			cntrl.ALU_Source = true;
			cntrl.ALU_Output = true;
			break;
		case SUB:
			cntrl.Reg_Write = true;
			cntrl.ALU_Source = true;
			cntrl.ALU_Output = true;
			break;
		case LD:
			cntrl.Read = true;
			cntrl.Reg_Write = true;
			cntrl.Mem_Read = true;
			cntrl.memRegWr = true;
			break;
		case SD:
			cntrl.Mem_Write = true;
			break;
		case BNEZ:
			cntrl.Read = true;
			cntrl.Branch = true;
			break;
		default:
			System.out.println("ERROR: Cannot set control signals for instruction of type " + name.toString());
			break;
		}
		return cntrl;
	}
	
	// Getter setters
	public final boolean isDestReg() {
		return Read;
	}

	public final void setDestReg(boolean destReg) {
		Read = destReg;
	}

	public final boolean isWritetoaReg() {
		return Reg_Write;
	}

	public final void setWritetoaReg(boolean writetoaReg) {
		Reg_Write = writetoaReg;
	}

	public final boolean isALUSrc() {
		return ALU_Source;
	}

	public final void setALUSrc(boolean aLUSrc) {
		ALU_Source = aLUSrc;
	}

	public final boolean isMemRd() {
		return Mem_Read;
	}

	public final void setMemRd(boolean memRd) {
		this.Mem_Read = memRd;
	}

	public final boolean isMemWrite() {
		return Mem_Write;
	}

	public final void setMemWrite(boolean memWrite) {
		this.Mem_Write = memWrite;
	}

	public final boolean isMemToReg() {
		return memRegWr;
	}

	public final void setMemToReg(boolean memToReg) {
		memRegWr = memToReg;
	}

	public final boolean isALUOp() {
		return ALU_Output;
	}

	public final void setALUOp(boolean aLUOp) {
		ALU_Output = aLUOp;
	}

	public final boolean isBranch() {
		return Branch;
	}

	public final void setBranch(boolean branch) {
		Branch = branch;
	}

}
