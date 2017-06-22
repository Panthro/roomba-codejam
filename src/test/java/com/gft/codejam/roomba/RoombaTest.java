package com.gft.codejam.roomba;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by panthro on 22/06/2017.
 */
public class RoombaTest {

    @Test
    public void testLoads() {

        final Roomba roomba = new Roomba();

        assertThat(roomba).isNotNull();


    }
}