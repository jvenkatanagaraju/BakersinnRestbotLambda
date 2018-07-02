package com.moxieit.orderplatform.helper;


	public class IdService {
		
		private static final long LIMIT = 100L;
		private static long last = 0;

		public static long getID() {
		  // 10 digits.
		  long id = System.currentTimeMillis() % LIMIT;
		  if ( id <= last ) {
		    id = (last + 1) % LIMIT;
		  }
		  return last = id;
		}

	}
