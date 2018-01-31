
package CompArch;

import java.util.LinkedList;

import CompArch.Processor;
import CompArch.Processor.Instruction_Name;
import Managers.IOManager;
import Managers.RegisterManager;

/**
 * @author Abhinav Gundlapalli
 * @id 1454321
 * @email agundlapalli@uh.edu
 */
public class Pipeline {

	public enum PipelineStage {
		IF1, IF2, ID, EX, MEM1, MEM2, MEM3, WB, COMPLETE
	};

	// Pipeline Registers
	private RegisterManager ALUresult, Reg_Src, Reg_Targ, Reg_Mem, Reg_Ins, Nxt_Field;
	private PipelineStage curState;
	private static LinkedList<Pipeline> pipelines = new LinkedList<Pipeline>();
	private int nxt_Reg;
	private Boolean ValAftrCond;
	private Processor InstProcObj;

	public Pipeline() {
		this.nxt_Reg = -1;
		this.ValAftrCond = null;
		this.ALUresult = new RegisterManager();
		;
		this.Reg_Mem = new RegisterManager();
		;
		this.Reg_Ins = new RegisterManager();
		;
		this.Nxt_Field = new RegisterManager();
		;
		this.Reg_Src = new RegisterManager();
		;
		this.Reg_Targ = new RegisterManager();
		;
	}

	public Pipeline(Processor i) {

		this();
		this.InstProcObj = i;
	}

	public Pipeline(String in) {
		this(); // default
		this.curState = Check_Param(in); // Check if passed parameter matches a
											// stage
	}

	public PipelineStage Check_Param(String s) {
		for (PipelineStage pist : PipelineStage.values()) {
			if (pist.name().equals(s)) {
				return pist;
			}
		}
		return null;
	}

	public void Next_Pipeline() {
		if (this.curState != null) {
			this.curState = this.curState.ordinal() < PipelineStage.values().length - 1
					? PipelineStage.values()[this.curState.ordinal() + 1] : null;
		} else {
			System.err.println("There was an eror trying to get next stage of an unspecified latch");
		}
	}

	public boolean Stall() {
		boolean stallNeeded = false;
		for (int i = 5; i < 7; i++) {
			Pipeline pipelineobj = IOManager.getAddress(i);
			if (pipelineobj != null) {
				Processor processorObj = pipelineobj.InstProcObj;
				if ((processorObj.getName() == Instruction_Name.LD) && (this.InstProcObj.getSrc1() != null)
						&& (processorObj.getDest().getReferenceReg() == this.InstProcObj.getSrc1().getReferenceReg())) {
					stallNeeded = true;
					break;
				} else if ((processorObj.getName() == Instruction_Name.LD) && (this.InstProcObj.getSrc2() != null)
						&& (processorObj.getDest().getReferenceReg() == this.InstProcObj.getSrc2().getReferenceReg())) {
					stallNeeded = true;
					break;
				}
			}
		}
		return stallNeeded;
	}

	/*public static void flushPipeLine() {
		Pipeline.getPipelines().set(1, null);
		Pipeline.getPipelines().set(2, null);
	}
*/
	public final int getNextReg() {
		return nxt_Reg;
	}

	public final void setNextReg(int nextReg) {
		this.nxt_Reg = nextReg;
	}

	public final Boolean getResAftrCond() {
		return ValAftrCond;
	}

	public final void setResAftrCond(Boolean resAftrCond) {
		this.ValAftrCond = resAftrCond;
	}

	public final Processor getInst() {
		return InstProcObj;
	}

	public final void setInst(Processor inst) {
		InstProcObj = inst;
	}

	public final RegisterManager getLdmemDataReg() {
		return Reg_Mem;
	}

	public final void setLdmemDataReg(RegisterManager ldmemDataReg) {
		this.Reg_Mem = ldmemDataReg;
	}

	public final RegisterManager getInstReg() {
		return Reg_Ins;
	}

	public final void setInstReg(RegisterManager instReg) {
		this.Reg_Ins = instReg;
	}

	public final RegisterManager getImediateField() {
		return Nxt_Field;
	}

	public final void setImediateField(RegisterManager imediateField) {
		Nxt_Field = imediateField;
	}

	public final RegisterManager getALUresult() {
		return ALUresult;
	}

	public final void setALUresult(RegisterManager aLUresult) {
		ALUresult = aLUresult;
	}

	public final RegisterManager getRegS() {
		return Reg_Src;
	}

	public final void setRegS(RegisterManager regS) {
		this.Reg_Src = regS;
	}

	public final RegisterManager getRegT() {
		return Reg_Targ;
	}

	public final void setRegT(RegisterManager regT) {
		this.Reg_Targ = regT;
	}

	public static final LinkedList<Pipeline> getPipelines() {
		return pipelines;
	}

	public static final void setPipelines(LinkedList<Pipeline> pipelines) {
		Pipeline.pipelines = pipelines;
	}

	public final PipelineStage getCurState() {
		return curState;
	}

	public final void setCurState(PipelineStage curState) {
		this.curState = curState;
	}
}