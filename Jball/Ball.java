// 	$Id: Ball.java,v 1.5 1998/09/07 07:50:43 dora Exp dora $	
package Jball;

class Ball {
	//  XYVector pos;                // position
	//  XYVector vel;               // velocity
	float[] pos = new float[2];
	float[] vel = new float[2];
	float q;                   // charge
	float m;                  // mass
	float dt = 1.0f;         // delta-t
	Field field;            // belonging field
	float vmax = 3f;       // permitted max velocity

	Ball(Field f, float x, float y) {
		this(f, x, y, 0.0f, 0.0f, 1.0f, 1.0f);
	}

	Ball(Field f, float x, float y, float vx, float vy) {
		this(f, x, y, vx, vy, 1.0f, 1.0f);
	}

	Ball(Field f, float x, float y, float vx, float vy, float q, float m) {
		pos[0] = x;  pos[1] = y;
		vel[0] = vx;  vel[1] = vy;
		this.q = 5f * q;
		this.m = m;
		this.field = f;
	}

	float[] getPosition() {
		return pos;
	}

	void move(float Ex, float Ey) {
		// checking the velocity
		//     float avx = (float)Math.abs(vel.x);
		//     float avy = (float)Math.abs(vel.y);
		//     vel.x = avx > vmax ? vmax * vel.x / avx : vel.x;
		//     vel.y = avy > vmax ? vmax * vel.y / avy : vel.y;

		// acceleration
		float ax = q/m * Ex;
		float ay = q/m * Ey;
		// velocity
		vel[0] += ax * dt;
		vel[1] += ay * dt;
		// position
		pos[0] += vel[0] * dt + 0.5 * ax * dt * dt;
		pos[1] += vel[1] * dt + 0.5 * ay * dt * dt;
	}
}
