/*
 * Copyright (C) 2017 P.Bernal-Polo
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package OrientationEstimators;

/**
 *
 * @author P.Bernal-Polo
 */
public class MUKFcMRP extends MUKF {
  
  // PROTECTED METHODS
  
  // Method: fM2C
  // defines the map from the manifold points, to the chart points
  // inputs:
  //  qm: mean quaternion of the distribution (is mapped with the origin of the chart)
  //  q: quaternion that we want to map with a point in the chart
  // outputs:
  //  e: point in the chart mapped with the q quaternion
  protected void fM2C( double[] e , double[] qm , double[] q ){
    // first we compute the delta in the manifold
    double[] delta = new double[4];
    delta[0] = qm[0]*q[0] + qm[1]*q[1] + qm[2]*q[2] + qm[3]*q[3];
    delta[1] = qm[0]*q[1]  -  q[0]*qm[1]  -  qm[2]*q[3] + qm[3]*q[2];
    delta[2] = qm[0]*q[2]  -  q[0]*qm[2]  -  qm[3]*q[1] + qm[1]*q[3];
    delta[3] = qm[0]*q[3]  -  q[0]*qm[3]  -  qm[1]*q[2] + qm[2]*q[1];
    // e from the chart definition: Modified Rodrigues Parameters
    if( delta[0] < 0.0 ) for(int i=0; i<4; i++) delta[i] = -delta[i];
    double aux = 4.0/( 1.0 + delta[0] );
    e[0] = delta[1]*aux;
    e[1] = delta[2]*aux;
    e[2] = delta[3]*aux;
    
    return;
  }
  
  // Method: fC2M
  // defines the map from the chart points, to the manifold points
  // inputs:
  //  qm: mean quaternion of the distribution (it is mapped with the origin of the chart)
  //  e: point of the chart that we want to map to a unit quaternion in the manifold
  // outputs:
  //  q: quaternion in the manifold mapped with the e point in the chart
  protected void fC2M( double[] q , double[] qm , double[] e ){
    // delta from the chart definition: Modified Rodrigues Parameters
    double enorm = Math.sqrt( e[0]*e[0] + e[1]*e[1] + e[2]*e[2] );
    if( enorm > 4.0 ){
      double aux = 4.0/enorm;
      e[0] = e[0]*aux;
      e[1] = e[1]*aux;
      e[2] = e[2]*aux;
      enorm = 4.0;
    }
    double aux = 1.0/( 16.0 + enorm*enorm );
    double[] delta = new double[4];
    delta[0] = ( 16.0 - enorm*enorm )*aux;
    delta[1] = 8.0*e[0]*aux;
    delta[2] = 8.0*e[1]*aux;
    delta[3] = 8.0*e[2]*aux;
    // now we update in the manifold with this delta
    q[0] = qm[0]*delta[0] - qm[1]*delta[1] - qm[2]*delta[2] - qm[3]*delta[3];
    q[1] = qm[0]*delta[1]  +  delta[0]*qm[1]  +  qm[2]*delta[3] - qm[3]*delta[2];
    q[2] = qm[0]*delta[2]  +  delta[0]*qm[2]  +  qm[3]*delta[1] - qm[1]*delta[3];
    q[3] = qm[0]*delta[3]  +  delta[0]*qm[3]  +  qm[1]*delta[2] - qm[2]*delta[1];
    
    return;
  }
  
}
