/*
 * MIT License
 *
 * Copyright (c) 2020 Ben Davies
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package net.bdavies.api.obj;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Discord Color Object
 *
 * @author me@bdavies.net (Ben Davies)
 * @since 3.0.0-rc.5
 */
@Slf4j
@RequiredArgsConstructor
public class DiscordColor
{
    public static final DiscordColor WHITE = DiscordColor.from(255, 255, 254);
    public static final DiscordColor LIGHT_GREY = DiscordColor.from(192, 192, 192);
    public final static DiscordColor GRAY = DiscordColor.from(128, 128, 128);
    public final static DiscordColor DARK_GRAY = DiscordColor.from(64, 64, 64);
    public final static DiscordColor BLACK = DiscordColor.from(0, 0, 0);
    public final static DiscordColor RED = DiscordColor.from(255, 0, 0);
    public final static DiscordColor PINK = DiscordColor.from(255, 175, 175);
    public final static DiscordColor ORANGE = DiscordColor.from(255, 200, 0);
    public final static DiscordColor YELLOW = DiscordColor.from(255, 255, 0);
    public final static DiscordColor GREEN = DiscordColor.from(0, 255, 0);
    public final static DiscordColor MAGENTA = DiscordColor.from(255, 0, 255);
    public final static DiscordColor CYAN = DiscordColor.from(0, 255, 255);
    public final static DiscordColor BLUE = DiscordColor.from(0, 0, 255);

    private final int r, g, b;

    /**
     * Create a Color based on a red green blue value
     *
     * @param red   the red value 0 to 255
     * @param green the green value 0 to 255
     * @param blue  the blue value 0 to 255
     * @return the new discord color
     */
    public static DiscordColor from(int red, int green, int blue)
    {
        if (red > 255)
        {
            red = 255;
        }
        if (green > 255)
        {
            green = 255;
        }
        if (blue > 255)
        {
            blue = 255;
        }

        return new DiscordColor(red, green, blue);
    }

    /**
     * Create a Color based on a hex value
     * {@code DiscordColor.from(0xFFFF00FF)}
     *
     * @param color the hex color to create
     * @return the new discord color
     */
    public static DiscordColor from(long color)
    {
        return new DiscordColor(
                ((short) (color >> 16) & 255),
                ((short) (color >> 8) & 255),
                ((short) (color) & 255)
        );
    }

    /**
     * Get the value of the color in RGB format
     *
     * @return the full hex format of the color
     */
    public int getRGB()
    {
        return ((short) r << 16) | ((short) g << 8) | (short) b;
    }

    public int getRed()
    {
        return r;
    }

    public int getGreen()
    {
        return g;
    }

    public int getBlue()
    {
        return b;
    }
}
