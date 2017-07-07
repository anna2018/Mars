package com.util;

import Jama.Matrix;
import Jama.SingularValueDecomposition;

public class Inverse {
	public static Matrix getInverse(Matrix m){
		SingularValueDecomposition n = m.svd();
		Matrix U=n.getU();
		Matrix V=n.getV();
		Matrix sga=n.getS();
		Matrix sga_inv = sga.inverse();
		Matrix temp= new Matrix(m.getRowDimension(),m.getColumnDimension());
		for(int i=0;i<sga_inv.getRowDimension();i++){
			for(int j=0;j<sga_inv.getColumnDimension();j++){
				temp.set(i, j, sga_inv.get(i, j));
			}
		}
		Matrix inv =V.times(temp).times(U.transpose());
		return inv;
	}
}
